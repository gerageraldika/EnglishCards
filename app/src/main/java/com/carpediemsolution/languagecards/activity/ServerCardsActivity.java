package com.carpediemsolution.languagecards.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.carpediemsolution.languagecards.App;
import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.R;
import com.carpediemsolution.languagecards.api.WebApi;
import com.carpediemsolution.languagecards.database.CardDBSchema;
import com.carpediemsolution.languagecards.pagination.PaginationScrollListener;
import com.carpediemsolution.languagecards.pagination.ServerCardsAdapter;
import com.carpediemsolution.languagecards.utils.CardUtils;
import com.carpediemsolution.languagecards.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Юлия on 31.03.2017.
 */

public class ServerCardsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = "ServerListActivity";

    private ProgressBar progressBar;
    private RecyclerView cardRecyclerView;
    private ServerCardsAdapter cardsAdapter;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 10;
    private int currentPage = PAGE_START;

    final private WebApi webApi = App.getWebApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_server);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor(getString(R.string.color_primary)));
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.server_cards_loading_progress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(getString(R.string.color_primary)), PorterDuff.Mode.SRC_IN);

        cardRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsActivity.this, 3);
        cardRecyclerView.setLayoutManager(gridLayoutManager);

        cardsAdapter = new ServerCardsAdapter(this);
        cardRecyclerView.setItemAnimator(new DefaultItemAnimator());
        cardRecyclerView.setAdapter(cardsAdapter);
        addOnScrollListener(cardRecyclerView, gridLayoutManager);
        downloadAllCardsFirstTime();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void downloadAllCardsFirstTime() {

        Call<List<Card>> call = webApi.getCards();
        progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    try {
                        List<Card> cards = new ArrayList<>();
                        cards.addAll(response.body());
                        cardsAdapter.addAll(cards);
                    } catch (NullPointerException e) {
                        System.out.print("no cards");
                    }
                }
                cardsAdapter.addLoadingFooter();
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ServerCardsActivity.this, R.string.error_inet,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadAllCardsNextTime() {
        Call<List<Card>> call = webApi.getCards();
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {

                progressBar.setVisibility(View.INVISIBLE);
                cardsAdapter.removeLoadingFooter();
                isLoading = false;
                if (response.isSuccessful()) {
                    try {
                        List<Card> cards = new ArrayList<>();
                        cards.addAll(response.body());
                        for (Card card : cards)
                            Log.d(LOG_TAG, "word " + card.getWord() + "desc " + card.getDescription());
                        cardsAdapter.addAll(cards);
                        cardsAdapter.addLoadingFooter();
                    } catch (NullPointerException e) {
                        Log.d(LOG_TAG, e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ServerCardsActivity.this, R.string.error_inet,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cards_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.action_stock): {
                Intent intent = new Intent(ServerCardsActivity.this, ServerCardsActivity.class);
                startActivity(intent);
                return true;
            }
            case (R.id.action_my_cards): {
                Intent intent = new Intent(ServerCardsActivity.this, UserCardsActivity.class);
                startActivity(intent);
                return true;
            }

            case (R.id.action_line): {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsActivity.this, 1);
                cardRecyclerView.setLayoutManager(gridLayoutManager);
                addOnScrollListener(cardRecyclerView, gridLayoutManager);
                return true;
            }
            case (R.id.action_frame): {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsActivity.this, 3);
                cardRecyclerView.setLayoutManager(gridLayoutManager);
                addOnScrollListener(cardRecyclerView, gridLayoutManager);
                return true;
            }
            case (R.id.action_settings): {
                SharedPreferences prefs = PreferenceManager.
                        getDefaultSharedPreferences(ServerCardsActivity.this);
                String token = prefs.getString(Preferences.TOKEN, "");
                Log.d(LOG_TAG, "---token " + token);
                if (CardUtils.isEmptyToken(token)) {
                    Intent intent = new Intent(ServerCardsActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ServerCardsActivity.this, UserAuthorizedActivity.class);
                    startActivity(intent);
                }
                return true;
            }
            case (R.id.action_about_app): {
                Intent intent = new Intent(ServerCardsActivity.this, InformationActivity.class);
                startActivity(intent);
                return true;
            }
            case (R.id.action_sync_cards): {
                Intent intent = new Intent(ServerCardsActivity.this, CardsSyncActivity.class);
                startActivity(intent);
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = new Intent(ServerCardsActivity.this, ServerSortedCardsActivity.class);
        Bundle b = new Bundle();
        switch (item.getItemId()) {
            case (R.id.all_items): {
                Intent intentAll = new Intent(ServerCardsActivity.this, ServerCardsActivity.class);
                startActivity(intentAll);
                break;
            }
            case (R.id.culture_art): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_CULTURE_ART); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            case (R.id.modern_technologies): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_MODERN_TECHNOLOGIES); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            case (R.id.society_politics): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_SOCIETY_POLITICS); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            case (R.id.adventure_travel): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_ADVENTURE_TRAVEL); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            case (R.id.nature_weather): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_NATURE_WEATHER); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            case (R.id.education_profession): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_EDUCATION_PROFESSION); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            case (R.id.appearance_character): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_APPEARANCE_CHARACTER); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            case (R.id.clothes_fashion): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_CLOTHES_FASHION); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            case (R.id.sport): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_SPORT); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            case (R.id.family_relationship): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_FAMILY_RELATIONSHIP); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            case (R.id.order_of_day): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_THE_ORDER_OF_DAY); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            case (R.id.hobbies_free_time): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_HOBBIES_FREE_TIME); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            case (R.id.customs_traditions): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_CUSTOMS_TRADITIONS); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            case (R.id.shopping): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_SHOPPING); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            case (R.id.food_drinks): {
                b.putString("card", CardDBSchema.CardTable.Themes.THEME_FOOD_DRINKS); //Your id
                intent.putExtras(b);
                startActivity(intent);
                break;
            }
            default:break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addOnScrollListener(RecyclerView recyclerView, GridLayoutManager gridLayoutManager) {
        recyclerView.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadAllCardsNextTime();
                    }
                }, 10);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
            finish();
        }
    }


}

