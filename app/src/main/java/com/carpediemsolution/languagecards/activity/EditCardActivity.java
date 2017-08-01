package com.carpediemsolution.languagecards.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.carpediemsolution.languagecards.App;
import com.carpediemsolution.languagecards.api.WebApi;
import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.dao.CardLab;
import com.carpediemsolution.languagecards.utils.CardUI;
import com.carpediemsolution.languagecards.R;
import com.carpediemsolution.languagecards.database.CardDBSchema;
import com.carpediemsolution.languagecards.utils.Preferences;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Юлия on 20.04.2017.
 */

public class EditCardActivity extends AppCompatActivity {

    private static final String LOG_TAG = "InsertActivity";
    private Card mCard;
    private InterstitialAd interstitial;

    @BindView(R.id.new_card_word)
    EditText editWord;
    @BindView(R.id.new_card_translate)
    EditText editTranslate;
    @BindView(R.id.new_card_description)
    EditText editDescription;

    @OnTextChanged(R.id.new_card_word)
    public void setCardWord(CharSequence s) {
        mCard.setWord(s.toString());
    }

    @OnTextChanged(R.id.new_card_translate)
    public void setCardTranslate(CharSequence s) {
        mCard.setTranslate(s.toString());
    }

    @OnTextChanged(R.id.new_card_translate)
    public void setCardDescription(CharSequence s) {
        mCard.setDescription(s.toString());
    }

    @OnClick(R.id.fab_new_card)
    public void onClick() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        } else {
            onClickWriteButton();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_new_card);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        String id = "";
        if (bundle != null)
            id = bundle.getString("card");

        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getString(R.string.admob));

        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                onClickWriteButton();
            }
        });

        CardUI cardUI = new CardUI();

        requestNewInterstitial();

        Toolbar toolbarTheme = (Toolbar) findViewById(R.id.toolbar_card_theme);
        setSupportActionBar(toolbarTheme);

        mCard = CardLab.get(EditCardActivity.this).getCard(id);

        editWord.setFilters(cardUI.setSizeForCardEditText());
        editTranslate.setFilters(cardUI.setSizeForCardEditText());
        editDescription.setFilters(cardUI.setSizeForCardDescriptionEditText());

        editWord.setText(mCard.getWord());
        editTranslate.setText(mCard.getTranslate());
        editDescription.setText(mCard.getDescription());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.android_action_bar_spinner_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        spinner.setPopupBackgroundResource(R.color.colorPrimary);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.titles_graph_tab, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                switch (selectedItem) {
                    case (Preferences.CULTURE_ART):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_CULTURE_ART);
                    case (Preferences.MODERN_TECHNOLOGIES):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_MODERN_TECHNOLOGIES);
                    case (Preferences.SOCIETY_POLITICS):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_SOCIETY_POLITICS);
                    case (Preferences.ADVENTURE_TRAVEL):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_ADVENTURE_TRAVEL);
                    case (Preferences.NATURE_WEATHER):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_NATURE_WEATHER);
                    case (Preferences.EDUCATION_PROFESSION):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_EDUCATION_PROFESSION);
                    case (Preferences.APPEARANCE_CHARACTER):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_APPEARANCE_CHARACTER);
                    case (Preferences.CLOTHES_FASHION):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_CLOTHES_FASHION);
                    case (Preferences.SPORT):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_SPORT);
                    case (Preferences.FAMILY_RELATIONSHIP):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_FAMILY_RELATIONSHIP);
                    case (Preferences.ORDER_OF_DAY):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_THE_ORDER_OF_DAY);
                    case (Preferences.HOBBIES_FREE_TIME):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_HOBBIES_FREE_TIME);
                    case (Preferences.CUSTOMS_TRADITIONS):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_CUSTOMS_TRADITIONS);
                    case (Preferences.SHOPPING):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_SHOPPING);
                    case (Preferences.FOOD_DRINKS):
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_FOOD_DRINKS);
                    default:
                        mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_CULTURE_ART);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return true;
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        interstitial.loadAd(adRequest);
    }

    protected void onClickWriteButton() {
        if (mCard.getWord() == null || mCard.getTranslate() == null) {
            Toast toast = Toast.makeText(EditCardActivity.this,
                    getString(R.string.insert_card), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (mCard.getTheme() == null) {
            Toast toast = Toast.makeText(EditCardActivity.this,
                    getString(R.string.insert_theme), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            CardLab.get(EditCardActivity.this).updateCard(mCard);

            final WebApi webApi = App.getWebApi();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences
                    (EditCardActivity.this);

            String token = prefs.getString(Preferences.TOKEN, "");

            if (token.equals("")) {
                token = prefs.getString(Preferences.ANON_TOKEN, "");

                if (token.equals("")) {
                    token = Preferences.ANONUM + new Date().toString();
                    prefs.edit().putString(Preferences.ANON_TOKEN, token).apply();
                }
            }
            Log.d(LOG_TAG, "---token " + token);

            Call<Card> callPost = webApi.updateCards(token, mCard);
            callPost.enqueue(new Callback<Card>() {
                @Override
                public void onResponse(Call<Card> call, Response<Card> response) {
                    Log.d(LOG_TAG, "---RESULT OK" + response.body());
                }

                @Override
                public void onFailure(Call<Card> call, Throwable t) {
                    Log.d(LOG_TAG, "---RESULT Failed");
                }
            });

            Intent intent = new Intent(EditCardActivity.this, UserCardsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}



