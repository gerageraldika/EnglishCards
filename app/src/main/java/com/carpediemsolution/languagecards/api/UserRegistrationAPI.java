package com.carpediemsolution.languagecards.api;

import com.carpediemsolution.languagecards.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Юлия on 16.04.2017.
 */

public interface UserRegistrationAPI {

    @POST("/languageapp/users/user")
    Call<ResponseBody> loadUser (@Body User user);
}
