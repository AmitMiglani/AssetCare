package com.toyota.assetcare.network;

/**
 * Created by Shubham_Aggarwal03 on 8/22/2018.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.toyota.assetcare.R;
import com.toyota.assetcare.utils.CommonConstants;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Shubham_Aggarwal03 on 8/8/2018.
 */

public class RetrofitHelperTextRecognition {

    private static Retrofit retrofit;

    public static Retrofit getInstance(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        String BASE_URL = "https://westus.api.cognitive.microsoft.com/vision/v1.0/";

        if(retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new TextRecognitionInterceptor(ctx.getString(R.string.subscription_key)))
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            return  retrofit;
        }
        return  retrofit;
    }

}
