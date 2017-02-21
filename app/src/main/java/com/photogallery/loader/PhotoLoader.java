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

import javax.inject.Inject;

public class PhotoLoader extends AsyncTaskLoader<Cursor> {

    private static final String TAG = PhotoLoader.class.getSimpleName();

    @Inject
    public ApiClient mApiClient;
    @Inject
    public DBHelper mDBHelper;

    private volatile int mPageToLoad = 1;

    private Cursor mCursor;

    public PhotoLoader(Context context) {
        super(context);
        CustomApplication.getAppComponent().inject(this);
    }

    @Override
    public Cursor loadInBackground() {

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + DBContractor.COLUMN_STAMP + " FROM " + DBContractor.TABLE_PHOTO + " WHERE " + DBContractor.COLUMN_STAMP + " = ?", new String[]{String.valueOf(hashCode())});

        if (cursor.getCount() == 0) {
            DebugLogger.d(TAG, "CLEAN DB");
            deleteAllRows(db);
        }
        cursor.close();

        cursor = db.rawQuery("SELECT " + DBContractor.COLUMN_CURRENT_PAGE + " FROM " + DBContractor.TABLE_PHOTO + " WHERE " + DBContractor.COLUMN_CURRENT_PAGE + " = ?", new String[]{String.valueOf(mPageToLoad)});

        if (cursor.getCount() == 0) {
            ParsedModel parsedModel = mApiClient.call(mPageToLoad);

            int currentPage = Integer.parseInt(parsedModel.getCurrentPageNumber());
            int totalPages = Integer.parseInt(parsedModel.getTotalPageNumber());

            DebugLogger.d(TAG, "loadInBackground page: " + mPageToLoad);

            if (parsedModel.getPhotoList().size() > 0) {
                transaction(db, parsedModel.getPhotoList(), currentPage, totalPages);
            }
        }

        cursor.close();

        return db.rawQuery("SELECT * FROM " + DBContractor.TABLE_PHOTO, null);
    }

    private void deleteAllRows(SQLiteDatabase db) {
        db.delete(DBContractor.TABLE_PHOTO, null, null);
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
        if (page > mPageToLoad) {
            DebugLogger.d(TAG, "load page: " + page);
            mPageToLoad = page;
            forceLoad();
        } else {
            DebugLogger.w(TAG, "Page " + page + " is already loaded!");
        }
    }
}
