package com.carpediemsolution.languagecards;

import android.app.Application;
import android.content.Context;

import com.carpediemsolution.languagecards.api.WebApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Юлия on 01.08.2017.
 */

public class App extends Application {

    private static WebApi webApi;
    private static Context appContext;

        @Override
        public void onCreate() {

        super.onCreate();
            Retrofit mRetrofit;
            //initialize retrofit client
            mRetrofit = new Retrofit.Builder()
                    .baseUrl("http://cards.carpediemsolutions.ru/")
                    //.baseUrl("http://,,")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            webApi = mRetrofit.create(WebApi.class);
            appContext = this;
    }
    public static WebApi getWebApi() {
        return webApi;
    }

    public static Context getAppContext() {
        return appContext;
    }
}
 