package com.carpediemsolution.languagecards.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.carpediemsolution.languagecards.App;
import com.carpediemsolution.languagecards.api.WebApi;
import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.dao.CardLab;
import com.carpediemsolution.languagecards.R;
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
 * Created by Юлия on 04.05.2017.
 */

public class CardsSyncActivity extends Activity {

    @BindView(R.id.sync_cards_progress)
    ProgressBar progressBar;

    @OnClick(R.id.action_sync_offline_cards)
    public void syncOfflineCards() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(CardsSyncActivity.this);
        String token = prefs.getString(Preferences.TOKEN, "");
        if (token.equals("")) {
            Toast.makeText(CardsSyncActivity.this, R.string.not_authorized,
                    Toast.LENGTH_SHORT).show();
        } else {
            syncCards();
        }
    }

    @OnClick(R.id.return_main_from_update_act)
    public void goToUserCsrdsActivity() {
        Intent intent = new Intent(CardsSyncActivity.this, UserCardsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        ButterKnife.bind(this);
    }

    protected void syncCards() {
        List<Card> userCards = CardLab.get(CardsSyncActivity.this).getCards();
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(CardsSyncActivity.this);
        String token = prefs.getString(Preferences.TOKEN, "");

        final WebApi webApi = App.getWebApi();
        Call<ResponseBody> callPost = webApi.postAllCardsToServer(token, userCards);
        progressBar.setVisibility(View.VISIBLE);
        callPost.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    try {
                        String s = response.body().string();
                        if (s.equals(Preferences.CARDS_ADDED)) {
                            Toast.makeText(CardsSyncActivity.this, R.string.sync_ok,
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CardsSyncActivity.this, UserCardsActivity.class);
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




