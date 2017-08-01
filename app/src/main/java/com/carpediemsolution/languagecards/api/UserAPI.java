package com.carpediemsolution.languagecards.api;

import com.carpediemsolution.languagecards.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Юлия on 16.04.2017.
 */

public interface UserAPI {

//повторный вход в приложение
    @POST("/languageapp/users/user/token")
    Call<ResponseBody> getUserToken(@Body User user);
}

