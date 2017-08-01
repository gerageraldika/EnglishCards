package com.carpediemsolution.languagecards.api;

import com.carpediemsolution.languagecards.model.Card;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Юлия on 16.04.2017.
 */

public interface UserCardsFromServerAPI {
    @POST("/languageapp/controller/user_cards")
    Call<List<Card>> getUserCardsFromServer (@Header("Token") String token, @Body List<Card> cards);
}
