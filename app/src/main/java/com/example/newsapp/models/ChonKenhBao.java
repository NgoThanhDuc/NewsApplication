package com.example.newsapp.models;

public class ChonKenhBao {
    private int imageNews;
    private String nameNews;

    public ChonKenhBao(int imageNews, String nameNews) {
        this.imageNews = imageNews;
        this.nameNews = nameNews;
    }

    public int getImageNews() {
        return imageNews;
    }

    public String getNameNews() {
        return nameNews;
    }
}
