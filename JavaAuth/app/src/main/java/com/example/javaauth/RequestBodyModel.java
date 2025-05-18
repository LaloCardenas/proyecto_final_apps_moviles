package com.example.javaauth;

import com.google.gson.annotations.SerializedName;

public class RequestBodyModel {

    @SerializedName("url")
    private String url;

    @SerializedName("user_id")
    private String user_id;

    public RequestBodyModel(String url, String user_id) {
        this.url = url;
        this.user_id = user_id;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return user_id;
    }
}
