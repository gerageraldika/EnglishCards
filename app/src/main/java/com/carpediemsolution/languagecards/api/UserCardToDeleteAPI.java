package com.carpediemsolution.languagecards.api;

import com.carpediemsolution.languagecards.model.Card;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Юлия on 23.04.2017.
 */

public interface UserCardToDeleteAPI {
    @POST("/languageapp/controller/delete")
    Call <ResponseBody>  deleteCard(@Header("Token") String token, @Body Card card);
}


