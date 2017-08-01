package com.carpediemsolution.languagecards.api;

import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;


/**
 * Created by Юлия on 27.03.2017.
 */

public interface WebApi {

    @GET("/languageapp/cards/all")
    Call<List<Card>> getCards();

    @GET("/languageapp/cards/all/{theme}")
    Call<List<Card>> getCardsByTheme(@Path("theme") String theme);

    @POST("/languageapp/controller/update")
    Call<Card> updateCards(@Header("Token") String token, @Body Card card);

    @POST("/languageapp/controller/post_new_card")
    Call<Card> uploadCards(@Header("Token") String token, @Body Card card);

    @POST("/languageapp/users/password")
    Call<ResponseBody> getUserPassword(@Body User user);

    //повторный вход в приложение
    @POST("/languageapp/users/user/token")
    Call<ResponseBody> getUserToken(@Body User user);

    @POST("/languageapp/controller/user_cards")
    Call<List<Card>> getUserCardsFromServer(@Header("Token") String token, @Body List<Card> cards);

    @POST("/languageapp/controller/post_all_cards")
    Call<ResponseBody> postAllCardsToServer(@Header("Token") String token, @Body List<Card> cards);

    @POST("/languageapp/controller/delete")
    Call<ResponseBody> deleteCard(@Header("Token") String token, @Body Card card);

    @POST("/languageapp/users/user")
    Call<ResponseBody> loadUser(@Body User user);
}
