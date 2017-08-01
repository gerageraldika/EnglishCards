package com.carpediemsolution.languagecards.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.dao.CardLab;
import com.carpediemsolution.languagecards.R;
import com.carpediemsolution.languagecards.model.User;
import com.carpediemsolution.languagecards.api.UserCardsToServerAPI;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * Created by Юлия on 17.04.2017.
 */

public class AuthorizedPersonActivity extends Activity {

    @BindView(R.id.authorized_progress)
    ProgressBar progressBar;
    @BindView(R.id.email)
    TextView userName;

    @OnClick(R.id.log_out_button)
    public void toLogOut() {
        toLoginOut();
    }

    @OnClick(R.id.forget_data)
    public void goToPasswordSenderActivity() {
        Intent intent = new Intent(AuthorizedPersonActivity.this, PasswordSenderActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.return_main)
    public void goToMainActivity() {
        Intent intent = new Intent(AuthorizedPersonActivity.this, CardsMainActivity.class);
        startActivity(intent);
    }

    User user = CardLab.get(AuthorizedPersonActivity.this).getUser();
    private static final String LOG_TAG = "AuthorizedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorized);
        ButterKnife.bind(this);

        userName.setText("Вы авторизированы в системе, " + user.getUsername());
    }

    private void sendUserCards() {

        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(AuthorizedPersonActivity.this);

        String token = prefs.getString("Token", "");
        List<Card> userCards = CardLab.get(AuthorizedPersonActivity.this).getCards();

        Retrofit client = CardLab.get(AuthorizedPersonActivity.this).getRetfofitClient();
        UserCardsToServerAPI serviceUpload = client.create(UserCardsToServerAPI.class);
        Call<ResponseBody> callPost = serviceUpload.postAllCardsToServer(token, userCards);
        progressBar.setVisibility(View.VISIBLE);
        callPost.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    try {
                        String s = response.body().string();
                        if (s.equals("cards added")) {
                            prefs.edit().remove("Token").apply();
                            CardLab.get(AuthorizedPersonActivity.this).deleteAllCards();
                            CardLab.get(AuthorizedPersonActivity.this).deleteUser();
                            Intent intent = new Intent(AuthorizedPersonActivity.this, CardsMainActivity.class);
                            startActivity(intent);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                userName.setText(user.getUsername() + ", отсутствует соединение с сервером.");
            }
        });

    }

    private void toLoginOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AuthorizedPersonActivity.this,R.style.MyTheme_Dark_Dialog);
        builder.setMessage("Внимание! Карточки привязаны к вашей учетной записи." +
                " Совершая выход из аккаунта, вы теряете возможность" +
                " просматривать карточки до следующего входа в систему.");
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendUserCards();
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        }).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AuthorizedPersonActivity.this, CardsMainActivity.class);
        startActivity(intent);
    }
}


