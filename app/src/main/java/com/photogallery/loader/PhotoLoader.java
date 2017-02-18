package com.photogallery.loader;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.photogallery.CustomApplication;
import com.photogallery.networking.ApiClient;
import com.photogallery.networking.ParsedModel;
import com.photogallery.util.DebugLogger;

import javax.inject.Inject;

public class PhotoLoader extends AsyncTaskLoader<ParsedModel> {

    private static final String TAG = PhotoLoader.class.getSimpleName();

    private ParsedModel mParsedModel;

    @Inject
    public ApiClient mApiClient;

    private volatile int currentPage = 1;

    public PhotoLoader(Context context) {
        super(context);
        CustomApplication.getAppComponent().inject(this);
    }

    @Override
    public ParsedModel loadInBackground() {
        ParsedModel parsedModel = mApiClient.call(currentPage);
        DebugLogger.d(TAG, "page: " + currentPage + ", response: " + parsedModel.getPhotoList().get(0).getUser().getFullName());
        return parsedModel;
    }

    @Override
    public void deliverResult(ParsedModel data) {
        mParsedModel = data;
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        DebugLogger.d(TAG, "start loading");
        if (mParsedModel != null) {
            deliverResult(mParsedModel);
        }
        if (takeContentChanged() || mParsedModel == null) {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        DebugLogger.d(TAG, "reset");
        onStopLoading();
        mParsedModel = null;
    }

    @Override
    protected void onStopLoading() {
        DebugLogger.d(TAG, "stop loading");
        cancelLoad();
    }

    public void loadPage(int page) {
        DebugLogger.d(TAG, "load page: " + page);
        currentPage = page;
        forceLoad();
    }
}
