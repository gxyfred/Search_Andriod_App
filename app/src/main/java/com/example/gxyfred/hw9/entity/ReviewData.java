package com.example.gxyfred.hw9.entity;

public class ReviewData {
    private String review_author_name;
    private String review_author_url;
    private String review_profile_photo_url;
    private int review_rating;
    private int review_default_order;
    private String review_text;
    private String review_time;

    public int compareTo(Object obj) {// Comparable接口中的方法
        ReviewData b = (ReviewData) obj;
        return this.review_default_order - b.review_default_order; // 按书的id比较大小，用于默认排序
    }

    public String getReview_author_name() {
        return review_author_name;
    }

    public void setReview_author_name(String review_author_name) {
        this.review_author_name = review_author_name;
    }

    public String getReview_author_url() {
        return review_author_url;
    }

    public void setReview_author_url(String review_author_url) {
        this.review_author_url = review_author_url;
    }

    public String getReview_profile_photo_url() {
        return review_profile_photo_url;
    }

    public void setReview_profile_photo_url(String review_profile_photo_url) {
        this.review_profile_photo_url = review_profile_photo_url;
    }

    public int getReview_rating() {
        return review_rating;
    }

    public void setReview_rating(int review_rating) {
        this.review_rating = review_rating;
    }

    public int getReview_default_order() {
        return review_default_order;
    }

    public void setReview_default_order(int review_default_order) {
        this.review_default_order = review_default_order;
    }

    public String getReview_text() {
        return review_text;
    }

    public void setReview_text(String review_text) {
        this.review_text = review_text;
    }

    public String getReview_time() {
        return review_time;
    }

    public void setReview_time(String review_time) {
        this.review_time = review_time;
    }
}
