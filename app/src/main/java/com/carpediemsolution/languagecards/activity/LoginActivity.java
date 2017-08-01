package com.carpediemsolution.languagecards.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carpediemsolution.languagecards.App;
import com.carpediemsolution.languagecards.api.WebApi;
import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.dao.CardLab;
import com.carpediemsolution.languagecards.UIUtils.CardUI;
import com.carpediemsolution.languagecards.R;
import com.carpediemsolution.languagecards.model.User;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Юлия on 25.03.2017.
 */

public class LoginActivity extends Activity {
    private AutoCompleteTextView usernameTextView;
    private EditText passwordTextView;
    private Button loginButton;
    private TextView forgetPasswordButton;
    private TextView signUpTextView;
    private ProgressBar progressBar;
    User user;

    private static final String LOG_TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = new User();
        progressBar = (ProgressBar) findViewById(R.id.login_progress);


        usernameTextView = (AutoCompleteTextView) findViewById(R.id.email);
        usernameTextView.setFilters(CardUI.setSizeForUserEditText());
        usernameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user.setUsername(s.toString());}
            @Override
            public void afterTextChanged(Editable s) {}
        });
        passwordTextView = (EditText) findViewById(R.id.password);
        passwordTextView.setFilters(CardUI.setSizeForUserEditText());
        passwordTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user.setPassword(s.toString());}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        signUpTextView = (TextView) findViewById(R.id.signUpTextView);
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);}
        });

        loginButton = (Button) findViewById(R.id.email_sign_in_button);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(usernameTextView.getText())) {
                    usernameTextView.setError("Введите логин");
                } else if (TextUtils.isEmpty(passwordTextView.getText())) {
                    passwordTextView.setError("Введите пароль");
                } else {
                    toLoginUser();}
            }
        });

        forgetPasswordButton = (TextView)findViewById(R.id.forget_data_in_login);
        forgetPasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PasswordSenderActivity.class);
                startActivity(intent);
            }
        });
    }

    private void toLoginUser(){
            final SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(LoginActivity.this);

           // final Retrofit client = CardLab.get(LoginActivity.this).getRetfofitClient();
           // UserAPI serviceUpload = client.create(UserAPI.class);
        final WebApi webApi = App.getWebApi();
           Call<ResponseBody> callPost = webApi.getUserToken(user);


            progressBar.setVisibility(View.VISIBLE);
            callPost.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        try {
                            String s = response.body().string();

                            if (s != null){
                            prefs.edit().putString("Token", s).apply();
                            prefs.edit().remove("AnonToken").apply();
                            CardLab.get(LoginActivity.this).addUser(user);
                            getUsersCards(s);}

                            else if (s == null){Toast.makeText(LoginActivity.this,
                                R.string.error_user_login,
                                    Toast.LENGTH_SHORT).show();}

                            Intent intent = new Intent(LoginActivity.this, AuthorizedPersonActivity.class);
                            startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            Toast.makeText(LoginActivity.this, R.string.error_user_login,
                                    Toast.LENGTH_SHORT).show();}
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, R.string.no_connection,
                            Toast.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, "---RESULT FAILED " + call.toString() + "---");}
            });
    }

    public void getUsersCards(final String token) {

        List<Card> anonCards = CardLab.get(LoginActivity.this).getCards();

        final WebApi webApi = App.getWebApi();
       // UserCardsFromServerAPI serviceUploadCards = client.create(UserCardsFromServerAPI.class);
        Call<List<Card>> call = webApi.getUserCardsFromServer(token,anonCards);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    try {
                        //апдейт нужен!!!
                        List<Card> cards = response.body();
                        CardLab.get(LoginActivity.this).addCards(cards);
                        Log.d(LOG_TAG, "---RESULT OK " + cards + " " + token);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, R.string.no_connection,
                        Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "---RESULT FAILED " + call.toString() + "---");}
        });

    }
}







