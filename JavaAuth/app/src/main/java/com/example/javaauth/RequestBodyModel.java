package com.example.javaauth;

import com.google.gson.annotations.SerializedName;

public class RequestBodyModel {

    @SerializedName("url")
    private String url;

    @SerializedName("username")
    private String username;

    public RequestBodyModel(String url, String username) {
        this.url = url;
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }
}
