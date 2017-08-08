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
import android.widget.TextView;
import android.widget.Toast;

import com.carpediemsolution.languagecards.App;
import com.carpediemsolution.languagecards.api.WebApi;
import com.carpediemsolution.languagecards.dao.CardLab;
import com.carpediemsolution.languagecards.utils.CardUI;
import com.carpediemsolution.languagecards.R;
import com.carpediemsolution.languagecards.model.User;
import com.carpediemsolution.languagecards.utils.CardUtils;
import com.carpediemsolution.languagecards.utils.Preferences;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Юлия on 25.03.2017.
 */

public class AuthorizationActivity extends Activity {

    private static final String LOG_TAG = "AuthorizationActivity";
    private AutoCompleteTextView usernameTextView;
    private EditText passwordTextView;
    private TextView userExistsTextView;
    private EditText repeatedPasswordView;
    private AutoCompleteTextView userEmailTextView;
    private CardUI cardUI;
    private User user = new User();
    private ProgressBar progressBar;

    @OnClick(R.id.email_sign_in_button)
    public void onClick() {

        String repeatPassword = repeatedPasswordView.getText().toString();
        Log.d(LOG_TAG, "---RepeatPassword" + repeatPassword);

        if (TextUtils.isEmpty(usernameTextView.getText())) {
            usernameTextView.setError(getString(R.string.error_null));
        } else if (TextUtils.isEmpty(passwordTextView.getText())) {
            passwordTextView.setError(getString(R.string.error_null));
        } else if (!cardUI.isEmailValid(user.getEmail())) {
            userEmailTextView.setError(getString(R.string.invalid_format));
        } else if (!repeatPassword.equals(user.getPassword())) {
            passwordTextView.setError("");
            repeatedPasswordView.setError("");
            Toast.makeText(AuthorizationActivity.this, getString(R.string.not_equal),
                    Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences
                    (AuthorizationActivity.this);
            String token = prefs.getString(Preferences.ANON_TOKEN, "");
            user.setToken(token);
            toSignUpUser();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        cardUI = new CardUI();
        progressBar = (ProgressBar) findViewById(R.id.signup_progress);

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

        userEmailTextView = (AutoCompleteTextView) findViewById(R.id.useremail);
        userEmailTextView.setFilters(cardUI.setSizeForUserEmailEditText());
        userEmailTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user.setEmail(s.toString());
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

        repeatedPasswordView = (EditText) findViewById(R.id.repeat_password);
        repeatedPasswordView.setFilters(cardUI.setSizeForUserEditText());
        repeatedPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        userExistsTextView = (TextView) findViewById(R.id.exist_login);
    }

    private void toSignUpUser() {
        final WebApi webApi = App.getWebApi();
        Call<ResponseBody> callPost = webApi.loadUser(user);
        progressBar.setVisibility(View.VISIBLE);
        callPost.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    try {
                        String s = response.body().string();
                        if (s.equals(Preferences.ALREADY__EXIST)) {
                            userExistsTextView.setText(s);
                        } else {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AuthorizationActivity.this);
                            prefs.edit().putString(Preferences.TOKEN, s).apply();
                            prefs.edit().remove(Preferences.ANON_TOKEN).apply();

                            Log.d(LOG_TAG, "---RESULT OK authorized token returned " + s);

                            CardLab.get().addUser(user);
                            String token = prefs.getString(Preferences.TOKEN, "");
                            if (!CardUtils.isEmptyToken(token)) {
                                Toast.makeText(AuthorizationActivity.this, R.string.welcome,
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AuthorizationActivity.this, UserAuthorizedActivity.class);
                                startActivity(intent);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Log.d(LOG_TAG, "---null response ");
                    }


                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(AuthorizationActivity.this, R.string.error_inet,
                        Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "---RESULT FAIL " + call.toString() + "---");
            }
        });
    }
}









