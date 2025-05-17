package com.example.javaauth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
/*
* Interfaz que será utilizada por la clase para ccontrolar las llamadas API
*
* */




public interface IApiHandler {

    @Headers("Content-Type: application/json")
    @POST("createURL")
    Call<Object> sendData(@Body RequestBodyModel body);


    @GET("getURLs")
    Call<Object> getData();

}
