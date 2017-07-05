package com.carpediemsolution.languagecards;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carpediemsolution.languagecards.api.UserRegistrationAPI;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Юлия on 25.03.2017.
 */

public class SignupActivity extends Activity {

    private static final String LOG_TAG = "SignupActivity";
    private AutoCompleteTextView usernameTextView;
    private EditText passwordTextView;
    private TextView userExistsTextView;
    private EditText repeatpasswordView;
    private Button loginButton;
    private AutoCompleteTextView useremailTextView;
    private String repeatPassword = "1";
    final User user = new User();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressBar = (ProgressBar) findViewById(R.id.signup_progress);

        usernameTextView = (AutoCompleteTextView) findViewById(R.id.email);
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

        useremailTextView = (AutoCompleteTextView) findViewById(R.id.useremail);
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

        passwordTextView = (EditText) findViewById(R.id.password);
        passwordTextView.setFilters(CardUI.setSizeForUserEditText());
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

        repeatpasswordView = (EditText) findViewById(R.id.repeat_password);
        repeatpasswordView.setFilters(CardUI.setSizeForUserEditText());
        repeatpasswordView.addTextChangedListener(new TextWatcher() {
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

        userExistsTextView =(TextView)findViewById(R.id.exist_login);

        loginButton = (Button) findViewById(R.id.email_sign_in_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatPassword = repeatpasswordView.getText().toString();
                Log.d(LOG_TAG, "---RepeatPassword" + repeatPassword);

                if (TextUtils.isEmpty(usernameTextView.getText())) {
                    usernameTextView.setError("Null");
                } else if (TextUtils.isEmpty(passwordTextView.getText())) {
                    passwordTextView.setError("Null");
                } else if (!CardUI.isEmailValid(user.getEmail())) {
                    useremailTextView.setError("Invalid format");
                } else if (!repeatPassword.equals(user.getPassword())) {
                    passwordTextView.setError("");
                    repeatpasswordView.setError("");
                    Toast.makeText(SignupActivity.this, "passwords are not equal ",
                            Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences
                            (SignupActivity.this);
                    String token = prefs.getString("AnonToken", "");
                    user.setToken(token);
                    toSignUpUser();
                }
            }
        });
    }

    private void toSignUpUser() {
        Retrofit client = CardLab.get(SignupActivity.this).getRetfofitClient();

        UserRegistrationAPI serviceUpload = client.create(UserRegistrationAPI.class);
        Call<ResponseBody> callPost = serviceUpload.loadUser(user);
        progressBar.setVisibility(View.VISIBLE);
        callPost.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    try {
                        String s = response.body().string();
                        if (s.equals("Данный логин уже существует")) {
                            userExistsTextView.setText(s);
                        } else {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SignupActivity.this);
                            prefs.edit().putString("Token", s).commit();
                            prefs.edit().remove("AnonToken").commit();

                        Log.d(LOG_TAG, "---RESULT OK authorized token returned " + s);

                        CardLab.get(SignupActivity.this).addUser(user);
                            String token = prefs.getString("Token", "");
                            if(token !=""){
                            Toast.makeText(SignupActivity.this, R.string.welcome,
                                Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, AuthorizedPersonActivity.class);
                            startActivity(intent);}
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
                Toast.makeText(SignupActivity.this, R.string.error_inet,
                        Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "---RESULT FAIL " + call.toString() + "---");
            }
        });
    }
}









