package com.example.newsapp.models;

public class ImagePlaceHolder {
    private String image;
    private String placeHolder;

    public ImagePlaceHolder(String image, String placeHolder) {
        this.image = image;
        this.placeHolder = placeHolder;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }
}
