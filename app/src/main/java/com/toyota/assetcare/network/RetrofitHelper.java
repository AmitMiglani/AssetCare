package com.toyota.assetcare.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.toyota.assetcare.network.BasicAuthInterceptor;
import com.toyota.assetcare.utils.CommonConstants;

/**
 * Created by Shubham_Aggarwal03 on 8/8/2018.
 */

public class RetrofitHelper {

    private static Retrofit retrofit;

    public static Retrofit getInstance(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String userName = prefs.getString(CommonConstants.EMAIL_SP, "toyotasurvey123@gmail.com");
        String token = prefs.getString(CommonConstants.TOKEN_SP, "25Qshz73UYXc5lWSiP0tB7FA");
        String BASE_URL = prefs.getString(CommonConstants.BASE_URL_SP, "https://toyotamanager.atlassian.net/");

         userName = "toyotasurvey123@gmail.com";
         token =  "8pYGEwKICApmZTSbAcnYC034";
         BASE_URL = "https://toyotamanager.atlassian.net/";

        if(retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new BasicAuthInterceptor(userName,token))
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
