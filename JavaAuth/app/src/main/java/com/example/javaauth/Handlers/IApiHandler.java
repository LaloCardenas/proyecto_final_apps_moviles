package com.example.javaauth.Handlers;

import com.example.javaauth.Models.CreateUrlRequest;
import com.example.javaauth.Models.GetUrlsRequest;
import com.example.javaauth.Models.UrlModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
/*
* Interfaz que será utilizada por la clase para ccontrolar las llamadas API
*
* */
public interface IApiHandler {

    @Headers("Content-Type: application/json")
    @POST("createURL")
    Call<Object> sendData(@Body CreateUrlRequest body);

    @Headers("Content-Type: application/json")
    @POST("getURLs")
    Call<List<UrlModel>> getUrls(@Body GetUrlsRequest body);

}
