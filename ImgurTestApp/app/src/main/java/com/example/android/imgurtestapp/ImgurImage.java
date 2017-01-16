package com.example.android.imgurtestapp;

/**
 * Created by Евгений on 28.12.2016.
 */

public class ImgurImage {

    private String imageUrl;
    private String title;

    public ImgurImage(String i, String t) {
        imageUrl = i;
        title = t;
    }

    public String getImage() {
        return imageUrl;
    }

    public String getTitle(){
        return title;
    }
}
