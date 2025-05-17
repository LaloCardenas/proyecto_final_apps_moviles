package com.example.javaauth;


/*
* Clase que va a manejar las llamdas a un API de aws por medio de retrofit
*
* */
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.os.StrictMode;
import software.amazon.awssdk.regions.Region;

public class ApiHandler {

    private final Retrofit retrofit;
    private final IApiHandler service;

    public ApiHandler() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AwsAuthInterceptor(String.valueOf(R.string.access), String.valueOf(R.string.secret), Region.US_EAST_1, "execute-api"))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://dw4pk0ul35.execute-api.us-east-1.amazonaws.com/default/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(IApiHandler.class);
    }

    public IApiHandler getService() {
        return service;
    }
}

