package com.carpediemsolution.languagecards.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.dao.CardLab;
import com.carpediemsolution.languagecards.R;
import com.carpediemsolution.languagecards.api.UserCardsToServerAPI;
import java.io.IOException;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Юлия on 04.05.2017.
 */

public class CardsSyncActivity extends Activity {

    private Button syncButton;
    private TextView returnMainActivityButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        syncButton = (Button) findViewById(R.id.action_sync_offline_cards);
        returnMainActivityButton = (TextView) findViewById(R.id.return_main_from_update_act);
        progressBar = (ProgressBar) findViewById(R.id.sync_cards_progress);

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(CardsSyncActivity.this);
                String token = prefs.getString("Token", "");
                if (token == "") {
                    Toast.makeText(CardsSyncActivity.this, R.string.not_authorized,
                            Toast.LENGTH_SHORT).show();
                } else {
                    syncCards();
                }
            }
        });

        returnMainActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardsSyncActivity.this, CardsMainActivity.class);
                startActivity(intent);
            }
        });

    }

    protected void syncCards() {
        List<Card> userCards = CardLab.get(CardsSyncActivity.this).getCards();
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(CardsSyncActivity.this);
        String token = prefs.getString("Token", "");

        Retrofit client = CardLab.get(CardsSyncActivity.this).getRetfofitClient();
        UserCardsToServerAPI serviceUpload = client.create(UserCardsToServerAPI.class);
        Call<ResponseBody> callPost = serviceUpload.postAllCardsToServer(token, userCards);
        progressBar.setVisibility(View.VISIBLE);
        callPost.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    try {
                        String s = response.body().string();
                        if (s.equals("cards added")) {
                            Toast.makeText(CardsSyncActivity.this, R.string.sync_ok,
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CardsSyncActivity.this, CardsMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                Toast.makeText(CardsSyncActivity.this, R.string.sync_cancel,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}




