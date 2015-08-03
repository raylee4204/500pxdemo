package com.example.demo500px.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Kanghee on 15-07-31.
 */
public class Photos {
    @SerializedName("current_page")
    private int currentPage;
    @SerializedName("total_pages")
    private int totalPages;
    private ArrayList<Photo> photos;

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }
}
