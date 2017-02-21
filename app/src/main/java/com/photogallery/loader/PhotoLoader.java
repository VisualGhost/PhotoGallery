package com.photogallery.loader;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.AsyncTaskLoader;

import com.photogallery.CustomApplication;
import com.photogallery.database.DBContractor;
import com.photogallery.database.DBHelper;
import com.photogallery.networking.ApiClient;
import com.photogallery.networking.ParsedModel;
import com.photogallery.networking.Photo;
import com.photogallery.util.DebugLogger;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Inject;

public class PhotoLoader extends AsyncTaskLoader<Cursor> {

    private static final String TAG = PhotoLoader.class.getSimpleName();
    private static final int INITIAL_PAGE = 1;
    private static final int NOT_VALID_PAGE = -1;

    @Inject
    public ApiClient mApiClient;
    @Inject
    public DBHelper mDBHelper;

    private final int mHashCode;
    private Cursor mCursor;

    private BlockingQueue<Integer> mPagesQue;
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    public PhotoLoader(Context context) {
        super(context);
        CustomApplication.getAppComponent().inject(this);
        mPagesQue = new LinkedBlockingQueue<>();
        mHandlerThread = new HandlerThread("", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        Looper looper = mHandlerThread.getLooper();

        mHandler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                int page = msg.arg1;
                if (!mPagesQue.contains(page)) {
                    mPagesQue.offer(page);
                    startLoading();
                }
            }

            private void startLoading() {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isStarted()) {
                            forceLoad();
                        }
                    }
                });
            }
        };
        mHashCode = hashCode();
    }

    @Override
    public Cursor loadInBackground() {

        DebugLogger.d(TAG, "loadInBackground: " + Thread.currentThread().getName());

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        boolean isDataCleaned = false;

        int pageToLoad;

        if (isDeleteCash(db, mHashCode)) {
            DebugLogger.d(TAG, "CLEAN DB");
            deleteAllRows(db);
            isDataCleaned = true;
            pageToLoad = INITIAL_PAGE;
        } else {
            pageToLoad = mPagesQue.size() > 0 ? mPagesQue.poll() : NOT_VALID_PAGE;
        }

        DebugLogger.d(TAG, "page: " + pageToLoad);

        if (pageToLoad != NOT_VALID_PAGE && (isDataCleaned || isNewPage(db, pageToLoad))) {
            DebugLogger.d(TAG, "loadInBackground page: " + pageToLoad);

            ParsedModel parsedModel = mApiClient.call(pageToLoad);
            if (parsedModel.isDataValid()) {
                handleParsedModel(db, parsedModel);
            } else {
                DebugLogger.w(TAG, "Data is invalid");
            }
        }

        return db.rawQuery("SELECT * FROM " + DBContractor.TABLE_PHOTO, null);
    }

    /**
     * Delete the cache when new loader is created
     */
    private boolean isDeleteCash(SQLiteDatabase db, int stamp) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT " + DBContractor.COLUMN_STAMP
                            + " FROM " + DBContractor.TABLE_PHOTO
                            + " WHERE " + DBContractor.COLUMN_STAMP + " = ?",
                    new String[]{String.valueOf(stamp)});
            return cursor.getCount() == 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void deleteAllRows(SQLiteDatabase db) {
        db.delete(DBContractor.TABLE_PHOTO, null, null);
    }

    private boolean isNewPage(SQLiteDatabase db, int page) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT " + DBContractor.COLUMN_CURRENT_PAGE
                            + " FROM " + DBContractor.TABLE_PHOTO
                            + " WHERE " + DBContractor.COLUMN_CURRENT_PAGE + " = ?",
                    new String[]{String.valueOf(page)});
            return cursor.getCount() == 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void handleParsedModel(SQLiteDatabase db, ParsedModel parsedModel) {
        int currentPage = Integer.parseInt(parsedModel.getCurrentPageNumber());
        int totalPages = Integer.parseInt(parsedModel.getTotalPageNumber());
        transaction(db, parsedModel.getPhotoList(), currentPage, totalPages);
    }

    private void transaction(SQLiteDatabase db, List<Photo> list, int currentPage, int totalPages) {

        db.beginTransaction();
        try {
            transactionBody(db, list, currentPage, totalPages);
            db.setTransactionSuccessful();
        } catch (IllegalStateException e) {
            DebugLogger.e(TAG, e.toString());
        } finally {
            db.endTransaction();
        }
    }

    private void transactionBody(SQLiteDatabase db, List<Photo> list, int currentPage, int totalPages) {
        savePhotos(db, list, currentPage, totalPages);
    }

    private void savePhotos(SQLiteDatabase db, List<Photo> list, int currentPage, int totalPages) {
        for (Photo photo : list) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBContractor.COLUMN_NAME, photo.getName());
            contentValues.put(DBContractor.COLUMN_URL, photo.getImageUrl());
            contentValues.put(DBContractor.COLUMN_CAMERA, photo.getCamera());
            contentValues.put(DBContractor.COLUMN_USER, photo.getUser().getFullName());
            contentValues.put(DBContractor.COLUMN_CURRENT_PAGE, currentPage);
            contentValues.put(DBContractor.COLUMN_TOTAL_PAGE, totalPages);
            contentValues.put(DBContractor.COLUMN_STAMP, hashCode());
            db.insert(DBContractor.TABLE_PHOTO, null, contentValues);
        }
    }

    @Override
    public void deliverResult(Cursor data) {
        super.deliverResult(data);
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = data;
    }

    @Override
    protected void onStartLoading() {
        DebugLogger.d(TAG, "start loading");
        forceLoad();
    }

    @Override
    protected void onReset() {
        DebugLogger.d(TAG, "reset");
        mHandlerThread.quit();
        mHandler.removeCallbacksAndMessages(null);
        onStopLoading();
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    protected void onStopLoading() {
        DebugLogger.d(TAG, "stop loading");
        cancelLoad();
    }

    public void loadPage(int page) {
        DebugLogger.d(TAG, "UI, load page: " + page);
        Message message = Message.obtain();
        message.arg1 = page;
        mHandler.sendMessage(message);
    }
}
