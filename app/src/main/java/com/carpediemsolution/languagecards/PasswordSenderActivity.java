package com.carpediemsolution.languagecards;

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
import com.carpediemsolution.languagecards.api.PasswordSenderAPI;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * Created by Юлия on 27.04.2017.
 */

public class PasswordSenderActivity extends Activity implements View.OnClickListener{

    private AutoCompleteTextView usernameTextView;
    private AutoCompleteTextView useremailTextView;
    private TextView passwordsendTextView;
    private ProgressBar progressBar;
    private static final String LOG_TAG = "PasswordSenderActivity";
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_sender_activity);

        user = new User();

        usernameTextView = (AutoCompleteTextView) findViewById(R.id.rememberlogin);
        useremailTextView = (AutoCompleteTextView) findViewById(R.id.rememberemail);
        passwordsendTextView = (TextView) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.password_progress);

        usernameTextView.setFilters(CardUI.setSizeForUserEditText());
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

        useremailTextView.setFilters(CardUI.setSizeForUserEmailEditText());
        useremailTextView.addTextChangedListener(new TextWatcher() {
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
        switch (v.getId()){
            case R.id.send_data_button:{
                if (TextUtils.isEmpty(usernameTextView.getText())) {
                    usernameTextView.setError("Null");
                } else if (!CardUI.isEmailValid(user.getEmail())) {
                    useremailTextView.setError("Invalid format");
                } else {
                    sendPasswordToEmail();
                }
                break;
            }
            case R.id.return__main_activity:{
                Intent intent = new Intent(PasswordSenderActivity.this, CardsMainActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.login_activity:{
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences
                        (PasswordSenderActivity.this);
                String token = prefs.getString("Token", "");
                if (token != "") {
                    User user = CardLab.get(PasswordSenderActivity.this).getUser();
                    Toast.makeText(PasswordSenderActivity.this, user.getUsername() + ", вы уже авторизированы в системе ",
                        Toast.LENGTH_SHORT).show();}
                else{
                Intent intent = new Intent(PasswordSenderActivity.this, LoginActivity.class);
                startActivity(intent);}
                break;
            }
        }
    }

            private void sendPasswordToEmail() {
                Retrofit client = CardLab.get(PasswordSenderActivity.this).getRetfofitClient();
                PasswordSenderAPI serviceUpload = client.create(PasswordSenderAPI.class);
                Call<ResponseBody> callPost = serviceUpload.getUserPassword(user);
                progressBar.setVisibility(View.VISIBLE);
                callPost.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            try {
                                String s = response.body().string();
                                Log.d(LOG_TAG, "отправка пароля на почту" + s);
                                if (s.equals("password was sent")) {
                                    findViewById(R.id.send_data_button).setVisibility(View.INVISIBLE);
                                    passwordsendTextView.setText("Пароль отправлен на вашу почту");
                                } else if (s.equals("password was not sent")) {
                                    passwordsendTextView.setText("Неверный логин или email");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(PasswordSenderActivity.this, "Отсутствует соединение, повторите попытку ",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
    }


