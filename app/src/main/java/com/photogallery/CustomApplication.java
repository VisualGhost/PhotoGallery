package com.photogallery;


import android.app.Application;

import com.facebook.stetho.Stetho;
import com.photogallery.di.AppComponent;
import com.photogallery.di.DIHelper;
import com.photogallery.util.DebugLogger;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DIHelper.init(this);
        initLogging();
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    public static AppComponent getAppComponent() {
        return DIHelper.getAppComponent();
    }

    private void initLogging(){
        if (BuildConfig.DEBUG) {
            DebugLogger.enableLogging();
        }
    }
}
