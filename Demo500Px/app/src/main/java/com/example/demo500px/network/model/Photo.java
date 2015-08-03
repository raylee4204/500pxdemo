package com.example.demo500px.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Kanghee on 15-07-31.
 */
public class Photo {

    private int id;
    private String name;
    @SerializedName ("images")
    private ArrayList<ImageFormat> imageFormats;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSmallImageUrl() {
        return imageFormats.get(0).getImageUrl();
    }

    public String getLargeImageUrl() {
        return imageFormats.get(1).getImageUrl();
    }

    public static class ImageFormat {

        public static int SIZE_440 = 440;
        public static int SIZE_1080 = 1080;

        private int size;
        @SerializedName ("https_url")
        private String url;

        public String getImageUrl() {
            return url;
        }

        public int getSize() {
            return size;
        }
    }

}

