package com.example.javaauth.Models;

import com.google.gson.annotations.SerializedName;

public class CreateUrlRequest {

    @SerializedName("url")
    private String url;

    @SerializedName("user_id")
    private String user_id;
    public CreateUrlRequest(String url, String user_id) {
        this.url = url;
        this.user_id = user_id;
    }

    public String getUrl() {
        return url;
    }
    public String getUser_id() {
        return user_id;
    }
}
