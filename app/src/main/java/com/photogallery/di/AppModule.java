package com.photogallery.di;

import com.photogallery.networking.ApiClient;
import com.photogallery.networking.ApiClientImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Singleton
    @Provides
    public ApiClient provideApiClient(){
        return new ApiClientImpl();
    }
}
