package com.carpediemsolution.languagecards.api;

import com.carpediemsolution.languagecards.Card;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


/**
 * Created by Юлия on 29.03.2017.
 */

public interface CardUpdateOnServerAPI {

    @POST("/languageapp/controller/update")
    Call <Card>  updateCards(@Header("Token") String token, @Body Card card);
    }
