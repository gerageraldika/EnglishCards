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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carpediemsolution.languagecards.App;
import com.carpediemsolution.languagecards.api.WebApi;
import com.carpediemsolution.languagecards.dao.CardLab;
import com.carpediemsolution.languagecards.utils.CardUI;
import com.carpediemsolution.languagecards.R;
import com.carpediemsolution.languagecards.model.User;
import com.carpediemsolution.languagecards.utils.Preferences;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Юлия on 27.04.2017.
 */

public class PasswordSenderActivity extends Activity implements View.OnClickListener {

    private AutoCompleteTextView usernameTextView;
    private AutoCompleteTextView userEmailTextView;
    private TextView passwordSendTextView;
    private ProgressBar progressBar;
    private CardUI cardUI;
    private static final String LOG_TAG = "PasswordSenderActivity";
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_sender_activity);

        user = new User();
        cardUI = new CardUI();

        usernameTextView = (AutoCompleteTextView) findViewById(R.id.rememberlogin);
        userEmailTextView = (AutoCompleteTextView) findViewById(R.id.rememberemail);
        passwordSendTextView = (TextView) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.password_progress);

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

        findViewById(R.id.send_data_button).setOnClickListener(this);
        findViewById(R.id.return__main_activity).setOnClickListener(this);
        findViewById(R.id.login_activity).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_data_button: {
                if (TextUtils.isEmpty(usernameTextView.getText())) {
                    usernameTextView.setError(getString(R.string.error_null));
                } else if (!cardUI.isEmailValid(user.getEmail())) {
                    userEmailTextView.setError(getString(R.string.invalid_format));
                } else {
                    sendPasswordToEmail();
                }
                break;
            }
            case R.id.return__main_activity: {
                Intent intent = new Intent(PasswordSenderActivity.this, UserCardsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.login_activity: {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences
                        (PasswordSenderActivity.this);
                String token = prefs.getString(Preferences.TOKEN, "");
                if (!token.equals("")) {
                    User user = CardLab.get().getUser();
                    Toast.makeText(PasswordSenderActivity.this, user.getUsername() + getString(R.string.already_authorized),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(PasswordSenderActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            }
        }
    }

    private void sendPasswordToEmail() {
        final WebApi webApi = App.getWebApi();
        Call<ResponseBody> callPost = webApi.getUserPassword(user);
        progressBar.setVisibility(View.VISIBLE);
        callPost.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    try {
                        String s = response.body().string();
                        Log.d(LOG_TAG, "отправка пароля на почту" + s);
                        if (s.equals(Preferences.PASSWORD_OK)) {
                            findViewById(R.id.send_data_button).setVisibility(View.INVISIBLE);
                            passwordSendTextView.setText(getString(R.string.password_is_sent));
                        } else if (s.equals(Preferences.PASSWORD_FAILTURE)) {
                            passwordSendTextView.setText(getString(R.string.invalid_pass_email));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(PasswordSenderActivity.this, getString(R.string.inet_failture),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}


