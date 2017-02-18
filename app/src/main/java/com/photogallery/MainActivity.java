package com.photogallery;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.photogallery.loader.PhotoLoader;
import com.photogallery.networking.ParsedModel;
import com.photogallery.util.DebugLogger;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ParsedModel> {

    private static int PHOTO_LOADER_ID = 0;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportLoaderManager().initLoader(PHOTO_LOADER_ID, null, this);
    }

    @Override
    public Loader<ParsedModel> onCreateLoader(int id, Bundle args) {
        return new PhotoLoader(getApplication());
    }

    @Override
    public void onLoadFinished(Loader<ParsedModel> loader, ParsedModel data) {
    }

    @Override
    public void onLoaderReset(Loader<ParsedModel> loader) {
    }
}
