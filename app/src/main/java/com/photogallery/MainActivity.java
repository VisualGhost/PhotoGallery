package com.photogallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.photogallery.networking.ApiClient;
import com.photogallery.networking.ParsedModel;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "https://api.500px.com/v1/photos?feature=popular&consumer_key=wB4ozJxTijCwNuggJvPGtBGCRqaZVcF6jsrzUadF&";

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
                ParsedModel parsedModel = mApiClient.call(URL);
                Log.d("Test", "parsed model: " + parsedModel.getPhotoList().get(0).getUser().getFullName());
            }
        }).start();

    }
}
