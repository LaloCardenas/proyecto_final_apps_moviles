package com.example.javaauth.Handlers;

/*
 * Clase para poder acceder al API de AWS
 *
 * */

import androidx.annotation.NonNull;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class AwsAuthInterceptor implements Interceptor {

    private final AwsCredentials awsCredentials;
    private final Region region;
    private final String serviceName;

    public AwsAuthInterceptor(String accessKey, String secretKey, Region region, String serviceName) {
        this.awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.region = region;
        this.serviceName = serviceName;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        //para realizar llamadas de http
        Request originalRequest = chain.request();
        //Para mandar credenciales de AWS
        Aws4Signer signer = Aws4Signer.create();
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.
                create(awsCredentials);

        //Consumir el url
        SdkHttpFullRequest.Builder requestBuilder = SdkHttpFullRequest.builder()
                .method(SdkHttpMethod.fromValue(originalRequest.method()))
                .uri(URI.create(originalRequest.url().toString()));

        // Agregar headers originales
        for (String name : originalRequest.headers().names()) {
            requestBuilder.appendHeader(name, originalRequest.header(name));
        }

        // Leer el contenido real del body
        if (originalRequest.body() != null) {

            okio.Buffer buffer = new okio.Buffer();

            originalRequest.body().writeTo(buffer);
            final String requestBodyString = buffer.readUtf8();

            if (!requestBodyString.isEmpty()) {
                requestBuilder.contentStreamProvider(() ->
                        new java.io.ByteArrayInputStream(requestBodyString.
                                getBytes(StandardCharsets.UTF_8))
                );
            }

        }

        // Firmar la request
        Aws4SignerParams signerParams = Aws4SignerParams.builder()
                .awsCredentials(awsCredentials)
                .signingName(serviceName)
                .signingRegion(region)
                .build();

        SdkHttpFullRequest signedRequest = signer.
                sign(requestBuilder.build(), signerParams);

        // Pasar los headers con las credenciales awss a la request de OkHttp
        Request.Builder newRequestBuilder = originalRequest.newBuilder();

        signedRequest.headers().forEach((key, values) -> {
            newRequestBuilder.removeHeader(key); // limpiar
            values.forEach(value -> newRequestBuilder.addHeader(key, value));
        });

        return chain.proceed(newRequestBuilder.build());
    }
}
