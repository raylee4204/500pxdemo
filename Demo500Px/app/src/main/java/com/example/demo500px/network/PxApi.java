package com.example.demo500px.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.example.demo500px.network.model.Photo;
import com.example.demo500px.network.model.Photos;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.AsyncHttpRequest;
import com.koushikdutta.async.http.Headers;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.loader.AsyncHttpRequestFactory;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * @author Kanghee
 */
public class PxApi {
    private static final String HOST = "https://api.500px.com/v1";
    private static final String CONSUMER_KEY = "XPVQlFu1rmPW4lH4k3aCGPcwkeE69N8FACn2OX1N";
    private static final String CONSUMER_SECRET = "1o14CfHPaH3iNbJ9zMKcsTi0bcGiPWsHYIzYWtAv";

    private static final String API_ENDPOINT_PHOTOS = "/photos";

    private static final TypeToken<Photos> TYPE_TOKEN_PHOTOS = new TypeToken<Photos>() {};

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

    public static void getPhotos(Fragment fragment, int page, FutureCallback<Response<Photos>> callback) {
        Uri.Builder uriBuilder = Uri.parse(HOST + API_ENDPOINT_PHOTOS)
                .buildUpon();
        Uri uri = uriBuilder.appendQueryParameter("consumer_key", CONSUMER_KEY)
                .appendQueryParameter("feature", "popular")
                .appendQueryParameter("page", String.valueOf(page))
                .appendQueryParameter("image_size",
                        Photo.ImageFormat.SIZE_440 + "," + Photo.ImageFormat.SIZE_1080)
                .build();

        Ion.with(fragment)
                .load("GET", uri.toString())
                .as(TYPE_TOKEN_PHOTOS)
                .withResponse()
                .setCallback(callback);
    }

    public static void getPhotos(Activity activity, int page, FutureCallback<Response<Photos>> callback) {
        Uri.Builder uriBuilder = Uri.parse(HOST + API_ENDPOINT_PHOTOS)
                .buildUpon();
        Uri uri = uriBuilder.appendQueryParameter("consumer_key", CONSUMER_KEY)
                .appendQueryParameter("feature", "popular")
                .appendQueryParameter("page", String.valueOf(page))
                .appendQueryParameter("image_size",
                        Photo.ImageFormat.SIZE_440 + "," + Photo.ImageFormat.SIZE_1080)
                .build();

        Ion.with(activity)
                .load("GET", uri.toString())
                .as(TYPE_TOKEN_PHOTOS)
                .withResponse()
                .setCallback(callback);
    }

}
