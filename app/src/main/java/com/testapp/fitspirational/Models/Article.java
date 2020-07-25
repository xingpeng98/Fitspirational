package com.testapp.fitspirational.Models;

public class Article {

    private String imgUrl;
    private String title;
    private String articleUrl;
    private String description;

    public Article(String imgUrl, String title, String articleUrl, String description) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.articleUrl = articleUrl;
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public String getDescription() {
        return description;
    }
}
