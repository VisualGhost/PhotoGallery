package com.photogallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.photogallery.networking.ApiClient;
import com.photogallery.networking.ApiClientImpl;
import com.photogallery.networking.ParsedModel;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "https://api.500px.com/v1/photos?feature=popular&consumer_key=wB4ozJxTijCwNuggJvPGtBGCRqaZVcF6jsrzUadF&";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiClient apiClient = new ApiClientImpl();
                ParsedModel parsedModel = apiClient.call(URL);
                Log.d("Test", "parsed model: " + parsedModel.getPhotoList().size());
            }
        }).start();

    }
}
