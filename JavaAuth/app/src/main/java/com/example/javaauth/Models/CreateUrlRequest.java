package com.example.javaauth.Models;

import com.google.gson.annotations.SerializedName;

/*
* Clase createURL
*
* crea el cuerpo de JSON para el request de crear un nuevo URL
* manda un url <- texto ingresado por el usuario
* manda el uuid <- se obtiene del firebase
* */
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
