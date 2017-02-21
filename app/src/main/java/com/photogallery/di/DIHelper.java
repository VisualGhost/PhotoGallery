package com.photogallery.di;

import android.content.Context;

public class DIHelper {

    private static AppComponent sAppComponent;

    public static void init(Context context) {
        sAppComponent = DaggerAppComponent.builder().appModule(new AppModule(context)).build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

}
