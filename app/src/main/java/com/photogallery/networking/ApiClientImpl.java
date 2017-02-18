package com.photogallery.networking;

import com.google.gson.Gson;
import com.photogallery.BuildConfig;
import com.photogallery.util.DebugLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiClientImpl implements ApiClient {

    private static String TAG = ApiClientImpl.class.getSimpleName();
    private static final String BASE_URL = BuildConfig.BASE_URL;

    @Override
    public ParsedModel call(int page) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(getUrlForPage(page)).build();
        ReceivedData receivedData = null;
        try {
            Response response = client.newCall(request).execute();


            if (response.isSuccessful()) {
                Reader in = response.body().charStream();
                BufferedReader reader = new BufferedReader(in);

                receivedData = new Gson().fromJson(reader, ReceivedData.class);
                reader.close();
            }
        } catch (IOException e) {
            DebugLogger.e(TAG, e.toString());
        }

        return new ParsedModelImpl(receivedData);
    }

    private String getUrlForPage(int page) {
        return String.format(BASE_URL, page);
    }
}
