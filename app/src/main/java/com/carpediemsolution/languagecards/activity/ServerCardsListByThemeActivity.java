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

public class ServerCardsListByThemeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String LOG_TAG = "ServerListActivity";
    private ProgressBar progressBar;
    private RecyclerView mCardRecyclerView;
    private ServerCardsAdapter mAdapter;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 10;
    private int currentPage = PAGE_START;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String theme = returnTheme();

        setContentView(R.layout.activity_cards_server);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#558B2F"));
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.server_cards_loading_progress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#558B2F"), PorterDuff.Mode.MULTIPLY);

        mCardRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
        mCardRecyclerView.setLayoutManager(gridLayoutManager);

        mAdapter = new ServerCardsAdapter(this);
        mCardRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCardRecyclerView.setAdapter(mAdapter);

        addOnScrollListener(mCardRecyclerView,gridLayoutManager,theme);

        downloadAllCardsFirstTime(theme);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void downloadAllCardsFirstTime(String theme)  {

        final WebApi webApi = App.getWebApi();
        Call<List<Card>> call = webApi.getCardsByTheme(theme);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    try {
                        List <Card> cards = new ArrayList<>();
                        cards.addAll(response.body());
                        mAdapter.addAll(cards);
                    } catch (NullPointerException e) {
                        System.out.print("no cards");
                    }
                }
                mAdapter.addLoadingFooter();
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ServerCardsListByThemeActivity.this, R.string.error_inet,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void downloadAllCardsNextTime(String theme) {
        final WebApi webApi = App.getWebApi();
        Call<List<Card>> call = webApi.getCardsByTheme(theme);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                progressBar.setVisibility(View.INVISIBLE);
                mAdapter.removeLoadingFooter();
                isLoading = false;
                if (response.isSuccessful()) {
                    try {
                        List <Card> cards = new ArrayList<>();
                        cards.addAll(response.body());
                        mAdapter.addAll(cards);
                    } catch (NullPointerException e) {
                        System.out.print("no cards");
                    }
                }
                mAdapter.addLoadingFooter();
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ServerCardsListByThemeActivity.this, R.string.error_inet,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cards_main, menu);
        return true;}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_stock) {
            Intent intent = new Intent(ServerCardsListByThemeActivity.this,ServerCardsListActivity.class);
            startActivity(intent);
        }
        if(id == R.id.action_my_cards){
            Intent intent = new Intent(ServerCardsListByThemeActivity.this,CardsMainActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_line) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,1);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);

            addOnScrollListener(mCardRecyclerView,gridLayoutManager,returnTheme());
            Log.d(LOG_TAG, "---theme in scroll " + returnTheme());
        }
        if (id == R.id.action_frame) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,returnTheme());
            Log.d(LOG_TAG, "---theme in scroll " + returnTheme());
        }
        if (id == R.id.action_settings) {
            SharedPreferences prefs = PreferenceManager.
                    getDefaultSharedPreferences(ServerCardsListByThemeActivity.this);
            String token = prefs.getString("Token", "");
            Log.d(LOG_TAG, "---token " + token);
            if (token.equals("")) {
                Intent intent = new Intent(ServerCardsListByThemeActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ServerCardsListByThemeActivity.this, AuthorizedPersonActivity.class);
                startActivity(intent);
            }
            return true;
        }
        if (id == R.id.action_about_app) {
            Intent intent = new Intent(ServerCardsListByThemeActivity.this, InformationActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_sync_cards) {
            Intent intent = new Intent(ServerCardsListByThemeActivity.this, CardsSyncActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent = new Intent(ServerCardsListByThemeActivity.this, ServerCardsListByThemeActivity.class);
        Bundle b = new Bundle();
        int id = item.getItemId();
        if (id == R.id.all_items) {
            Intent intentAllCards = new Intent(ServerCardsListByThemeActivity.this,ServerCardsListActivity.class);
            startActivity(intentAllCards);
        }
        else if (id == R.id.culture_art) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_CULTURE_ART);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_CULTURE_ART);
        }
        else if (id == R.id.modern_technologies) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_MODERN_TECHNOLOGIES);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_MODERN_TECHNOLOGIES);
        }
        else if (id == R.id.society_politics) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_SOCIETY_POLITICS);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_SOCIETY_POLITICS);
        }
        else if (id == R.id.adventure_travel) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_ADVENTURE_TRAVEL);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_ADVENTURE_TRAVEL);

        } else if (id == R.id.nature_weather) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_NATURE_WEATHER);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_NATURE_WEATHER);
        } else if (id == R.id.education_profession) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_EDUCATION_PROFESSION);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_EDUCATION_PROFESSION);

        } else if (id == R.id.appearance_character) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_APPEARANCE_CHARACTER);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_APPEARANCE_CHARACTER);

        } else if (id == R.id.clothes_fashion) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_CLOTHES_FASHION);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_CLOTHES_FASHION);

        } else if (id == R.id.sport) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_SPORT);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_SPORT);

        } else if (id == R.id.family_relationship) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_FAMILY_RELATIONSHIP);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_FAMILY_RELATIONSHIP);
        } else if (id == R.id.order_of_day) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_THE_ORDER_OF_DAY);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_THE_ORDER_OF_DAY);

        } else if (id == R.id.hobbies_free_time) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_HOBBIES_FREE_TIME);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_HOBBIES_FREE_TIME);

        } else if (id == R.id.customs_traditions) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_CUSTOMS_TRADITIONS);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_CUSTOMS_TRADITIONS);
        } else if (id == R.id.shopping) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_SHOPPING);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_SHOPPING);
        } else if (id == R.id.food_drinks) {
            mAdapter.clear();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListByThemeActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            downloadAllCardsFirstTime(CardDBSchema.CardTable.Themes.THEME_FOOD_DRINKS);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager,CardDBSchema.CardTable.Themes.THEME_FOOD_DRINKS);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addOnScrollListener(RecyclerView recyclerView, GridLayoutManager gridLayoutManager, final String theme){
        recyclerView.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadAllCardsNextTime(theme);}
                },10);
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
            finish();}
    }

    private String returnTheme(){
        Bundle bundle = getIntent().getExtras();
        String theme = "";
        if(bundle != null)
            theme = bundle.getString("card");
        return theme;
    }

}
