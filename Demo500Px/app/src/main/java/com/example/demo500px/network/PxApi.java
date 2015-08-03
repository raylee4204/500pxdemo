package com.example.demo500px.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.koushikdutta.async.http.AsyncHttpRequest;
import com.koushikdutta.async.http.Headers;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.loader.AsyncHttpRequestFactory;

import android.content.Context;
import android.net.Uri;

/**
 * @author Kanghee
 */
public class PxApi {
    private static final String HOST = "https://api.500px.com/v1";
    private static final String CONSUMER_KEY = "XPVQlFu1rmPW4lH4k3aCGPcwkeE69N8FACn2OX1N";
    private static final String CONSUMER_SECRET = "1o14CfHPaH3iNbJ9zMKcsTi0bcGiPWsHYIzYWtAv";

    private static final String API_ENDPOINT_PHOTOS = "/photos";

    public static void initHttpClient(Context context) {

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Ion.getDefault(context)
                .configure()
                .setAsyncHttpRequestFactory(new AsyncHttpRequestFactory() {
                    @Override
                    public AsyncHttpRequest createAsyncHttpRequest(Uri uri, String method,
                            Headers headers) {
                        AsyncHttpRequest request = new AsyncHttpRequest(uri, method, headers);
                        request.setTimeout(5000);
                        return request;
                    }
                })
                .setGson(gson);
    }
}
