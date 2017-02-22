package com.photogallery.loader;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;

import com.photogallery.CustomApplication;
import com.photogallery.database.DBContractor;
import com.photogallery.database.DBHelper;
import com.photogallery.networking.ApiClient;
import com.photogallery.networking.ParsedModel;
import com.photogallery.networking.Photo;
import com.photogallery.util.DebugLogger;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

public class PhotoLoader extends AsyncTaskLoader<Cursor> {

    private static final String TAG = PhotoLoader.class.getSimpleName();
    private static final int INITIAL_PAGE = 1;
    private static final int NOT_VALID_PAGE = -1;

    @Inject
    public ApiClient mApiClient;
    @Inject
    public DBHelper mDBHelper;

    private Cursor mCursor;
    private final AtomicBoolean mIsCleanCache;
    private final ConcurrentLinkedQueue<Integer> mPagesQue;

    public PhotoLoader(Context context) {
        super(context);
        CustomApplication.getAppComponent().inject(this);
        mPagesQue = new ConcurrentLinkedQueue<>();
        mPagesQue.add(INITIAL_PAGE);
        mIsCleanCache = new AtomicBoolean(true);
    }

    @Override
    public Cursor loadInBackground() {

        DebugLogger.d(TAG, "loadInBackground: " + Thread.currentThread().getName() + ", " + mPagesQue);

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        boolean isDataCleaned = false;

        Integer integer = mPagesQue.poll();
        int pageToLoad = integer != null ? integer : NOT_VALID_PAGE;

        if (mIsCleanCache.get()) {
            mIsCleanCache.set(false);
            DebugLogger.d(TAG, "CLEAN DB");
            deleteAllRows(db);
            isDataCleaned = true;
            pageToLoad = INITIAL_PAGE;
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
        DebugLogger.d(TAG, "UI: load page " + page + ", " + mPagesQue);
        if (!mPagesQue.contains(page)) {
            mPagesQue.add(page);
            forceLoad();
        }
    }
}
