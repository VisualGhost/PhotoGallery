package com.photogallery.networking;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiClientImpl implements ApiClient {

    @Override
    public ParsedModel call(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
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
            // TODO add logging
        }

        // TODO do we need to create a new object every time?
        return new ParsedModelImpl(receivedData);
    }
}
