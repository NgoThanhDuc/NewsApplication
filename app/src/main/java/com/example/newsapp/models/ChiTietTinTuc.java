package com.example.newsapp.models;

public class ChiTietTinTuc {
    private String newsName;
    private String title;
    private String link;
    private String image;
    private String pubDate;

    public ChiTietTinTuc(String newsName, String title, String link, String image, String pubDate) {
        this.newsName = newsName;
        this.title = title;
        this.link = link;
        this.image = image;
        this.pubDate = pubDate;
    }

    public String getNewsName() {
        return newsName;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getImage() {
        return image;
    }

    public String getPubDate() {
        return pubDate;
    }
}
