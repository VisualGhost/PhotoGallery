package com.photogallery;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.photogallery.database.DBContractor;
import com.photogallery.loader.PhotoLoader;
import com.photogallery.view.adapter.PhotoAdapter;
import com.photogallery.view.layout.PhotoGalleryLayout;
import com.photogallery.view.pagination.PaginationListener;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        PaginationListener {

    private static final int PHOTO_LOADER_ID = 1;

    private PhotoAdapter mAdapter;
    private PhotoGalleryLayout mPhotoGalleryLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new PhotoAdapter();
        mPhotoGalleryLayout = (PhotoGalleryLayout) findViewById(R.id.photo_grid_layout_id);
        mPhotoGalleryLayout.setAdapter(mAdapter);
        mPhotoGalleryLayout.setPaginationListener(this);
        getSupportLoaderManager().initLoader(PHOTO_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new PhotoLoader(getApplication());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToLast()) {
            int currentPageIndex = data.getColumnIndex(DBContractor.COLUMN_CURRENT_PAGE);
            int currentPage = currentPageIndex != -1 ? data.getInt(currentPageIndex) : -1;
            mPhotoGalleryLayout.setCurrentPage(currentPage);

            int totalPageIndex = data.getColumnIndex(DBContractor.COLUMN_TOTAL_PAGE);
            int totalPages = totalPageIndex != -1 ? data.getInt(totalPageIndex) : -1;
            mPhotoGalleryLayout.setTotalPage(totalPages);
        }

        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // empty
    }

    @Override
    public void loadPage(int page) {
        Loader<Cursor> loader = getSupportLoaderManager().getLoader(PHOTO_LOADER_ID);
        ((PhotoLoader) loader).loadPage(page);
    }
}
