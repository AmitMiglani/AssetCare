package com.toyota.assetcare.network;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TextRecognitionInterceptor implements Interceptor {


    private final String key;

    public TextRecognitionInterceptor(String key) {
        this.key = key;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Content-Type", "application/octet-stream").header("ocp-apim-subscription-key",key).build();
        return chain.proceed(authenticatedRequest);
    }

}