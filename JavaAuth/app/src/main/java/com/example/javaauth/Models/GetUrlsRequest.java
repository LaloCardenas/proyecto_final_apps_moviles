package com.example.javaauth.Models;

import com.google.gson.annotations.SerializedName;

public class GetUrlsRequest {

    @SerializedName("user_id")
    private String userId;

    public GetUrlsRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
