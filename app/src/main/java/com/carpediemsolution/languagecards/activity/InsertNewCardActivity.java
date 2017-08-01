package com.carpediemsolution.languagecards.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.dao.CardLab;
import com.carpediemsolution.languagecards.UIUtils.CardUI;
import com.carpediemsolution.languagecards.R;
import com.carpediemsolution.languagecards.api.FileUploadService;
import com.carpediemsolution.languagecards.database.CardDBSchema;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Date;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InsertNewCardActivity extends AppCompatActivity {

    private static final String LOG_TAG = "InsertActivity";
    private Toolbar toolbarTheme;
    private EditText editWord;
    private EditText editTranslate;
    private EditText editDescription;
    private Card mCard;
    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_new_card);

        toolbarTheme = (Toolbar) findViewById(R.id.toolbar_card_theme);
        setSupportActionBar(toolbarTheme);

        mCard = new Card();
        mCard.setId(String.valueOf(UUID.randomUUID()));
        mCard.setPerson_id(0);

        editWord = (EditText) findViewById(R.id.new_card_word);
        editTranslate = (EditText) findViewById(R.id.new_card_translate);
        editDescription = (EditText) findViewById(R.id.new_card_description);

        editWord.setFilters(CardUI.setSizeForCardEditText());
        editTranslate.setFilters(CardUI.setSizeForCardEditText());
        editDescription.setFilters(CardUI.setSizeForCardDescriptionEditText());

        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("cca-app-pub-9016583513972837/6074106306");

        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                onClickWriteButton();
            }
        });

        requestNewInterstitial();

        editWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCard.setWord(s.toString());}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        editTranslate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCard.setTranslate(s.toString());}
            @Override
            public void afterTextChanged(Editable s) {}
        });


        FloatingActionButton fabWriteCard = (FloatingActionButton) findViewById(R.id.fab_new_card);

        editDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCard.setDescription(s.toString());
               }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        fabWriteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitial.isLoaded()) {
                    interstitial.show();}
                else{onClickWriteButton();}
            }
        });

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

                                                  if(selectedItem.equals("Culture and Art")){
                                                  mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_CULTURE_ART);}
                                                  if(selectedItem.equals("Modern technologies")){
                                                      mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_MODERN_TECHNOLOGIES);}
                                                  if(selectedItem.equals("Society and Politics")){
                                                      mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_SOCIETY_POLITICS);}
                                                  if(selectedItem.equals("Adventure and Travel")){
                                                      mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_ADVENTURE_TRAVEL);}
                                                  if(selectedItem.equals("Nature and Weather")){
                                                      mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_NATURE_WEATHER);}
                                                  if(selectedItem.equals("Education and Profession")){
                                                      mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_EDUCATION_PROFESSION);}
                                                  if(selectedItem.equals("Appearance and Character")){
                                                      mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_APPEARANCE_CHARACTER);}
                                                  if(selectedItem.equals("Clothes and Fashion")){
                                                      mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_CLOTHES_FASHION);}
                                                  if(selectedItem.equals("Sport")){
                                                      mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_SPORT);}
                                                  if(selectedItem.equals("Family and Relationship")){
                                                      mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_FAMILY_RELATIONSHIP);}
                                                  if(selectedItem.equals("The order of the day")){
                                                      mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_THE_ORDER_OF_DAY);}
                                                  if(selectedItem.equals("Hobbies and Free time")){
                                                      mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_HOBBIES_FREE_TIME);}
                                                  if(selectedItem.equals("Customs and Traditions")){
                                                      mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_CUSTOMS_TRADITIONS);}
                                                  if(selectedItem.equals("Shopping")){
                                                      mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_SHOPPING);}
                                                  if(selectedItem.equals("Food and Drinks")){
                                                      mCard.setTheme(CardDBSchema.CardTable.Themes.THEME_FOOD_DRINKS);}
                                              }

                                              @Override
                                              public void onNothingSelected(AdapterView<?> parent) {

                                              } });
        return true;}

    protected void onClickWriteButton(){
        if (mCard.getWord() == null || mCard.getTranslate() == null) {
            Toast toast = Toast.makeText(InsertNewCardActivity.this,
                    getString(R.string.insert_card), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (mCard.getTheme() == null) {
            Toast toast = Toast.makeText(InsertNewCardActivity.this,
                    getString(R.string.insert_theme), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            CardLab.get(InsertNewCardActivity.this).addCard(mCard);
            Log.d(LOG_TAG, "---addCard" + mCard.getWord());
            Retrofit client = CardLab.get(InsertNewCardActivity.this).getRetfofitClient();

            FileUploadService serviceUpload = client.create(FileUploadService.class);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences
                    (InsertNewCardActivity.this);

            String token = prefs.getString("Token", "");

            if (token.equals("")) {
                token = prefs.getString("AnonToken", "");

                if (token.equals("")) {
                    token = "anonym " + new Date().toString();
                    prefs.edit().putString("AnonToken", token).apply();
                }
            }
            Log.d(LOG_TAG, "---token " + " -" + token + "- ");

            Call<Card> callPost = serviceUpload.uploadCards(token, mCard);
            callPost.enqueue(new Callback<Card>() {
                @Override
                public void onResponse(Call<Card> call, Response<Card> response) {
                    Log.d(LOG_TAG, "---RESULT OK" + response.body());}
                @Override
                public void onFailure(Call<Card> call, Throwable t) {
                    Log.d(LOG_TAG, "---RESULT Failed");}
            });

            Intent intent = new Intent(InsertNewCardActivity.this, CardsMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("3EED2099D69A864B")
                .build();
        interstitial.loadAd(adRequest);
    }

}
