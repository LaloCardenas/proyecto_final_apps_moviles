    package com.example.javaauth.Handlers;


    /*
    * Clase que va a manejar las llamdas a un API de aws por medio de retrofit
    *
    * */
    import okhttp3.OkHttpClient;
    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;

    import android.content.Context;

    import com.example.javaauth.R;

    import software.amazon.awssdk.regions.Region;

    public class ApiHandler {

        private final Retrofit retrofit;
        private final IApiHandler service;

        public ApiHandler(Context context) {
            //Credenciales
            String accessKey = context.getString(R.string.access);
            String secretKey = context.getString(R.string.secret);

            //clase para consumir llamadas al http
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AwsAuthInterceptor(accessKey, secretKey, Region.US_EAST_1, "execute-api"))
                    .build();
            //retrofir para consumir el api
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

