package com.photogallery.loader;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.content.AsyncTaskLoader;

import com.photogallery.CustomApplication;
import com.photogallery.database.DBHelper;
import com.photogallery.networking.ApiClient;
import com.photogallery.networking.ParsedModel;
import com.photogallery.networking.Photo;
import com.photogallery.util.DebugLogger;

import java.util.List;

import javax.inject.Inject;

public class PhotoLoader extends AsyncTaskLoader<Cursor> {

    private static final String TAG = PhotoLoader.class.getSimpleName();

    private Cursor mCursor;

    @Inject
    public ApiClient mApiClient;
    @Inject
    public DBHelper mDBHelper;

    private volatile int currentPage = 1;

    private HandlerThread mHandlerThread;
    private Handler backgroundHandler;

    public PhotoLoader(Context context) {
        super(context);
        CustomApplication.getAppComponent().inject(this);
        mHandlerThread = new HandlerThread("HandlerLoaderThread", android.os.Process.THREAD_PRIORITY_BACKGROUND){
            @Override
            protected void onLooperPrepared() {
                super.onLooperPrepared();
                backgroundHandler = new Handler(getLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        
                    }
                };
            }
        };
    }

    @Override
    public Cursor loadInBackground() {

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.Tables.PAGE + " WHERE page = ?", new String[]{String.valueOf(currentPage)});

        if (cursor.getCount() == 0) {
            ParsedModel parsedModel = mApiClient.call(currentPage);
            DebugLogger.d(TAG, "loadInBackground page: " + currentPage);

            if (parsedModel.getPhotoList().size() > 0) {
                transaction(db, parsedModel.getPhotoList());
            }
        }

        cursor.close();

        return db.rawQuery("SELECT * FROM " + DBHelper.Tables.PHOTO, null);
    }

    private void transaction(SQLiteDatabase db, List<Photo> list) {

        db.beginTransaction();
        try {
            transactionBody(db, list);
            db.setTransactionSuccessful();
        } catch (IllegalStateException e) {
            DebugLogger.e(TAG, e.toString());
        } finally {
            db.endTransaction();
        }
    }

    private void transactionBody(SQLiteDatabase db, List<Photo> list) {
        if (currentPage == 1) {
            cleanDb(db);
        }
        savePhotos(db, list);
        savePages(db);
    }

    private void cleanDb(SQLiteDatabase db) {
        db.delete(DBHelper.Tables.PHOTO, null, null);
        db.delete(DBHelper.Tables.PAGE, null, null);
    }

    private void savePhotos(SQLiteDatabase db, List<Photo> list) {
        for (Photo photo : list) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.Columns.NAME, photo.getName());
            contentValues.put(DBHelper.Columns.URL, photo.getImageUrl());
            contentValues.put(DBHelper.Columns.CAMERA, photo.getCamera());
            contentValues.put(DBHelper.Columns.USER, photo.getUser().getFullName());
            db.insert(DBHelper.Tables.PHOTO, null, contentValues);
        }
    }

    private void savePages(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.Columns.LOADED_PAGE, currentPage);
        db.insert(DBHelper.Tables.PAGE, null, contentValues);
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
        if (mCursor != null) {
            DebugLogger.d(TAG, "start loading: " + mCursor.isClosed());
        } else {
            DebugLogger.d(TAG, "start loading");
        }
        if (mCursor != null && !mCursor.isClosed()) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null || mCursor.isClosed()) {
            DebugLogger.d(TAG, "forceLoad");
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        DebugLogger.d(TAG, "reset");
        onStopLoading();
        mCursor.close();
        mCursor = null;
    }

    @Override
    protected void onStopLoading() {
        DebugLogger.d(TAG, "stop loading");
        cancelLoad();
    }

    public void loadPage(int page) {
        if (page > currentPage) {
            DebugLogger.d(TAG, "load page: " + page);
            currentPage = page;
            forceLoad();
        } else {
            DebugLogger.w(TAG, "Page " + page + " is already loaded!");
        }
    }
}
