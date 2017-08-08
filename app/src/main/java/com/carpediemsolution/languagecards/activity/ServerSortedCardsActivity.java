package com.carpediemsolution.languagecards.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import com.carpediemsolution.languagecards.api.WebApi;
import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.R;
import com.carpediemsolution.languagecards.database.CardDBSchema;
import com.carpediemsolution.languagecards.pagination.PaginationScrollListener;
import com.carpediemsolution.languagecards.pagination.ServerCardsAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Юлия on 18.05.2017.
 */

public class ServerSortedCardsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String LOG_TAG = "ServerListActivity";
    private ProgressBar progressBar;
    private RecyclerView cardsRecyclerView;
    private ServerCardsAdapter cardsAdapter;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 10;
    private int currentPage = PAGE_START;
    private final WebApi webApi = App.getWebApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String theme = returnTheme();

        setContentView(R.layout.activity_cards_server);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor(String.valueOf(getString(R.string.color_primary))));
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.server_cards_loading_progress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(String.valueOf(getString(R.string.color_primary))), PorterDuff.Mode.MULTIPLY);

        cardsRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
        cardsRecyclerView.setLayoutManager(gridLayoutManager);

        cardsAdapter = new ServerCardsAdapter(this);
        cardsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        cardsRecyclerView.setAdapter(cardsAdapter);

        addOnScrollListener(cardsRecyclerView, gridLayoutManager, theme);

        downloadAllCardsFirstTime(theme);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void downloadAllCardsFirstTime(String theme) {

        Call<List<Card>> call = webApi.getCardsByTheme(theme);
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
                Toast.makeText(ServerSortedCardsActivity.this, R.string.error_inet,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void downloadAllCardsNextTime(String theme) {
        Call<List<Card>> call = webApi.getCardsByTheme(theme);
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
                        cardsAdapter.addAll(cards);
                    } catch (NullPointerException e) {
                        Log.d(LOG_TAG, e.toString());
                    }
                }
                cardsAdapter.addLoadingFooter();
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ServerSortedCardsActivity.this, R.string.error_inet,
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
                Intent intent = new Intent(ServerSortedCardsActivity.this, ServerCardsActivity.class);
                startActivity(intent);
                return true;
            }
            case (R.id.action_my_cards): {
                Intent intent = new Intent(ServerSortedCardsActivity.this, UserCardsActivity.class);
                startActivity(intent);
                return true;
            }

            case (R.id.action_line): {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 1);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);

                addOnScrollListener(cardsRecyclerView, gridLayoutManager, returnTheme());
                Log.d(LOG_TAG, "---theme in scroll " + returnTheme());
                return true;
            }
            case (R.id.action_frame): {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, returnTheme());
                Log.d(LOG_TAG, "---theme in scroll " + returnTheme());
                return true;
            }
            case (R.id.action_settings): {
                SharedPreferences prefs = PreferenceManager.
                        getDefaultSharedPreferences(ServerSortedCardsActivity.this);
                String token = prefs.getString("Token", "");
                Log.d(LOG_TAG, "---token " + token);
                if (token.equals("")) {
                    Intent intent = new Intent(ServerSortedCardsActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ServerSortedCardsActivity.this, UserAuthorizedActivity.class);
                    startActivity(intent);
                }
                return true;
            }
            case (R.id.action_about_app): {
                Intent intent = new Intent(ServerSortedCardsActivity.this, InformationActivity.class);
                startActivity(intent);
                return true;
            }
            case (R.id.action_sync_cards): {
                Intent intent = new Intent(ServerSortedCardsActivity.this, CardsSyncActivity.class);
                startActivity(intent);
                return true;
            }
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.all_items): {
                Intent intentAllCards = new Intent(ServerSortedCardsActivity.this, ServerCardsActivity.class);
                startActivity(intentAllCards);
                break;
            }
            case (R.id.culture_art): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_CULTURE_ART);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_CULTURE_ART);
                break;
            }
            case (R.id.modern_technologies): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_MODERN_TECHNOLOGIES);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_MODERN_TECHNOLOGIES);
                break;
            }
            case (R.id.society_politics): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_SOCIETY_POLITICS);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_SOCIETY_POLITICS);
                break;
            }
            case (R.id.adventure_travel): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_ADVENTURE_TRAVEL);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_ADVENTURE_TRAVEL);
                break;
            }
            case (R.id.nature_weather): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_NATURE_WEATHER);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_NATURE_WEATHER);
                break;
            }
            case (R.id.education_profession): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_EDUCATION_PROFESSION);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_EDUCATION_PROFESSION);
                break;
            }
            case (R.id.appearance_character): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_APPEARANCE_CHARACTER);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_APPEARANCE_CHARACTER);
                break;
            }
            case (R.id.clothes_fashion): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_CLOTHES_FASHION);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_CLOTHES_FASHION);
                break;
            }
            case (R.id.sport): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_SPORT);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_SPORT);
                break;
            }
            case (R.id.family_relationship): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_FAMILY_RELATIONSHIP);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_FAMILY_RELATIONSHIP);
                break;
            }
            case (R.id.order_of_day): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_THE_ORDER_OF_DAY);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_THE_ORDER_OF_DAY);
                break;
            }
            case (R.id.hobbies_free_time): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_HOBBIES_FREE_TIME);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_HOBBIES_FREE_TIME);
                break;
            }
            case (R.id.customs_traditions): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_CUSTOMS_TRADITIONS);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_CUSTOMS_TRADITIONS);
                break;
            }
            case (R.id.shopping): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_SHOPPING);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_SHOPPING);
                break;
            }
            case (R.id.food_drinks): {
                cardsAdapter.clear();
                GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerSortedCardsActivity.this, 3);
                cardsRecyclerView.setLayoutManager(gridLayoutManager);
                downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_FOOD_DRINKS);
                addOnScrollListener(cardsRecyclerView, gridLayoutManager, CardDBSchema.CardTable.Themes.THEME_FOOD_DRINKS);
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addOnScrollListener(RecyclerView recyclerView, GridLayoutManager gridLayoutManager, final String theme) {
        recyclerView.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadAllCardsNextTime(theme);
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

    private String returnTheme() {
        Bundle bundle = getIntent().getExtras();
        String theme = "";
        if (bundle != null)
            theme = bundle.getString("card");
        return theme;
    }

}
