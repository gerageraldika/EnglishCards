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
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.carpediemsolution.languagecards.App;
import com.carpediemsolution.languagecards.api.WebApi;
import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.dao.CardLab;
import com.carpediemsolution.languagecards.utils.CardUI;
import com.carpediemsolution.languagecards.R;
import com.carpediemsolution.languagecards.model.User;
import com.carpediemsolution.languagecards.utils.Preferences;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    private User user;

    private final WebApi webApi = App.getWebApi();
    private static final String LOG_TAG = "LoginActivity";

    @BindView(R.id.login_progress)
    ProgressBar progressBar;

    @OnClick(R.id.email_sign_in_button)
    public void toLogInUser() {
        if (TextUtils.isEmpty(usernameTextView.getText())) {
            usernameTextView.setError(getString(R.string.insert_login));
        } else if (TextUtils.isEmpty(passwordTextView.getText())) {
            passwordTextView.setError(getString(R.string.insert_password));
        } else {
            toLoginUser();
        }
    }

    @OnClick(R.id.signUpTextView)
    public void goToAuthorizationActivity() {
        Intent intent = new Intent(LoginActivity.this, AuthorizationActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.signUpTextView)
    public void goToPasswordSenderActivity() {
        Intent intent = new Intent(LoginActivity.this, PasswordSenderActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        user = new User();
        CardUI cardUI = new CardUI();

        usernameTextView = (AutoCompleteTextView) findViewById(R.id.email);
        usernameTextView.setFilters(cardUI.setSizeForUserEditText());
        usernameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user.setUsername(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        passwordTextView = (EditText) findViewById(R.id.password);
        passwordTextView.setFilters(cardUI.setSizeForUserEditText());
        passwordTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user.setPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void toLoginUser() {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(LoginActivity.this);

        Call<ResponseBody> callPost = webApi.getUserToken(user);
        progressBar.setVisibility(View.VISIBLE);
        callPost.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    try {
                        String s = response.body().string();

                        if (s != null) {
                            prefs.edit().putString(Preferences.TOKEN, s).apply();
                            prefs.edit().remove(Preferences.ANON_TOKEN).apply();
                            CardLab.get(LoginActivity.this).addUser(user);
                            getUsersCards(s);
                        } else if (s == null) {
                            Toast.makeText(LoginActivity.this,
                                    R.string.error_user_login,
                                    Toast.LENGTH_SHORT).show();
                        }

                        Intent intent = new Intent(LoginActivity.this, UserAuthorizedActivity.class);
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Toast.makeText(LoginActivity.this, R.string.error_user_login,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, R.string.no_connection,
                        Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "---RESULT FAILED " + call.toString() + "---");
            }
        });
    }

    public void getUsersCards(final String token) {
        List<Card> anonCards = CardLab.get(LoginActivity.this).getCards();

        Call<List<Card>> call = webApi.getUserCardsFromServer(token, anonCards);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    try {
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
                Log.d(LOG_TAG, "---RESULT FAILED " + call.toString() + "---");
            }
        });
    }
}







