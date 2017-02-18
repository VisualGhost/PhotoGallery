package com.photogallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.photogallery.networking.ApiClient;
import com.photogallery.networking.ParsedModel;
import com.photogallery.util.DebugLogger;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @Inject
    public ApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomApplication.getAppComponent().inject(this);


        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ParsedModel parsedModel = mApiClient.call(1);
                DebugLogger.d("Test", "parsed model: " + parsedModel.getPhotoList().get(0).getUser().getFullName());
            }
        }).start();

    }
}
