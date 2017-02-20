package com.photogallery.di;

import android.content.Context;

import com.photogallery.database.DBHelper;
import com.photogallery.networking.ApiClient;
import com.photogallery.networking.ApiClientImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Context mContext;

    public AppModule(Context context) {
        mContext = context;
    }

    @Singleton
    @Provides
    public ApiClient provideApiClient(){
        return new ApiClientImpl();
    }

    @Singleton
    @Provides
    public DBHelper provideDBHelper(){
        return new DBHelper(mContext.getApplicationContext());
    }
}
