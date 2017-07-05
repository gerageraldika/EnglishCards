package com.carpediemsolution.languagecards.api;
import com.carpediemsolution.languagecards.Card;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;


/**
 * Created by Юлия on 27.03.2017.
 */

public interface API {

    @GET("/languageapp/cards/all")
    Call<List<Card>>  getCards();
}
