package com.photogallery.di;

import com.photogallery.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(MainActivity mainActivity);

}
