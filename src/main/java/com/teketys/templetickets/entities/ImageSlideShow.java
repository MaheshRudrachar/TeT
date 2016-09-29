package com.teketys.templetickets.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rudram1 on 9/2/16.
 */

public class ImageSlideShow implements Parcelable{

    private String title;
    private String link;
    private String image;

    public ImageSlideShow() {

    }

    public ImageSlideShow(String title, String link, String image) {
        this.title = title;
        this.link = link;
        this.image = image;
    }

    private ImageSlideShow(Parcel in) {
        super();
        this.title = in.readString();
        this.link = in.readString();
        this.image = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getTitle());
        parcel.writeString(getLink());
        parcel.writeString(getImage());
    }

    public static final Parcelable.Creator<ImageSlideShow> CREATOR = new Parcelable.Creator<ImageSlideShow>() {
        public ImageSlideShow createFromParcel(Parcel in) {
            return new ImageSlideShow(in);
        }

        public ImageSlideShow[] newArray(int size) {
            return new ImageSlideShow[size];
        }
    };
}
