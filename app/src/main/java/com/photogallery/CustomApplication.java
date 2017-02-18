package com.photogallery;


import android.app.Application;

import com.photogallery.di.AppComponent;
import com.photogallery.di.DIHelper;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DIHelper.init();
    }

    public static AppComponent getAppComponent() {
        return DIHelper.getAppComponent();
    }
}
