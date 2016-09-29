package com.teketys.templetickets.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rudram1 on 9/2/16.
 */

public class ImageSlideShowResponse {

    @SerializedName("data")
    public List<ImageSlideShow> getImageSlideShows() {
        return imageSlideShows;
    }

    public void setImageSlideShows(List<ImageSlideShow> imageSlideShows) {
        this.imageSlideShows = imageSlideShows;
    }

    private List<ImageSlideShow> imageSlideShows;
}
