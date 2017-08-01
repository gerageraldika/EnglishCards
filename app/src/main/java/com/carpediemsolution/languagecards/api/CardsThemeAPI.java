package com.carpediemsolution.languagecards.api;

import com.carpediemsolution.languagecards.model.Card;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Юлия on 02.04.2017.
 */

public interface CardsThemeAPI {

    @GET("/languageapp/cards/all/{theme}")
    Call<List<Card>> getCardsByTheme(@Path("theme") String theme);
}
