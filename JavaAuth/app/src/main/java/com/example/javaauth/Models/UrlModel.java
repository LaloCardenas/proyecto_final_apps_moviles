package com.example.javaauth.Models;

import com.google.gson.annotations.SerializedName;

public class UrlModel {

    @SerializedName("short")
    private String shortUrl;

    @SerializedName("url")
    private String originalUrl;

    @SerializedName("expire_at")
    private long expire;

    public String getShortUrl() {
        return shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public long getExpire(){
        return expire;
    }

}

