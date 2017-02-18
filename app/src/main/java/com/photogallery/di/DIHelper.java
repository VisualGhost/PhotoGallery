package com.photogallery.di;

public class DIHelper {

    private static AppComponent sAppComponent;

    public static void init() {
        sAppComponent = DaggerAppComponent.builder().build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

}
