package com.photogallery.di;

import com.photogallery.MainActivity;
import com.photogallery.loader.PhotoLoader;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(PhotoLoader photoLoader);

}
