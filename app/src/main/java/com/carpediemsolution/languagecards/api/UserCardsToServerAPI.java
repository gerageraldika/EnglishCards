package com.carpediemsolution.languagecards.api;

import com.carpediemsolution.languagecards.model.Card;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Юлия on 23.04.2017.
 */

public interface UserCardsToServerAPI {
    @POST("/languageapp/controller/post_all_cards")
    Call<ResponseBody> postAllCardsToServer(@Header("Token") String token, @Body List<Card> cards);
}

