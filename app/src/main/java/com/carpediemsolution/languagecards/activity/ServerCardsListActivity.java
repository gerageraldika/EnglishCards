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

import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.dao.CardLab;
import com.carpediemsolution.languagecards.R;
import com.carpediemsolution.languagecards.api.API;
import com.carpediemsolution.languagecards.database.CardDBSchema;
import com.carpediemsolution.languagecards.pagination.PaginationScrollListener;
import com.carpediemsolution.languagecards.pagination.ServerCardsAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Юлия on 31.03.2017.
 */

public class ServerCardsListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = "ServerListActivity";

    private ProgressBar progressBar;
    private RecyclerView mCardRecyclerView;
    private ServerCardsAdapter mAdapter;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 10;
    private int currentPage = PAGE_START;
   // private List<Card> cardList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_server);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#558B2F"));
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.server_cards_loading_progress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#558B2F"), PorterDuff.Mode.SRC_IN);

        mCardRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListActivity.this,3);
        mCardRecyclerView.setLayoutManager(gridLayoutManager);

        mAdapter = new ServerCardsAdapter(this);
        mCardRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCardRecyclerView.setAdapter(mAdapter);
        addOnScrollListener(mCardRecyclerView,gridLayoutManager);
        downloadAllCardsFirstTime();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void downloadAllCardsFirstTime()  {
        API service = CardLab.get(ServerCardsListActivity.this).getRetfofitClient().create(API.class);
        Call<List<Card>> call = service.getCards();
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
                Toast.makeText(ServerCardsListActivity.this, R.string.error_inet,
                        Toast.LENGTH_SHORT).show();}
        });
    }

    private void downloadAllCardsNextTime() {
        API service = CardLab.get(ServerCardsListActivity.this).getRetfofitClient().create(API.class);
        Call<List<Card>> call = service.getCards();
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
                        for (Card card:cards)
                        Log.d(LOG_TAG, "word " + card.getWord() + "desc " + card.getDescription());
                        mAdapter.addAll(cards);
                        mAdapter.addLoadingFooter();
                    } catch (NullPointerException e) {
                        System.out.print("no cards");}
                }
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ServerCardsListActivity.this, R.string.error_inet,
                        Toast.LENGTH_SHORT).show();}
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
            Intent intent = new Intent(ServerCardsListActivity.this,ServerCardsListActivity.class);
            startActivity(intent);
        }
        if(id == R.id.action_my_cards){
            Intent intent = new Intent(ServerCardsListActivity.this,CardsMainActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_line) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListActivity.this,1);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager);
        }
        if (id == R.id.action_frame) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ServerCardsListActivity.this,3);
            mCardRecyclerView.setLayoutManager(gridLayoutManager);
            addOnScrollListener(mCardRecyclerView,gridLayoutManager);
        }
        if (id == R.id.action_settings) {
            SharedPreferences prefs = PreferenceManager.
                    getDefaultSharedPreferences(ServerCardsListActivity.this);
            String token = prefs.getString("Token", "");
            Log.d(LOG_TAG, "---token " + token);
            if (token.equals("")) {
                Intent intent = new Intent(ServerCardsListActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ServerCardsListActivity.this, AuthorizedPersonActivity.class);
                startActivity(intent);
            }
            return true;
        }
        if (id == R.id.action_about_app) {
            Intent intent = new Intent(ServerCardsListActivity.this, InformationActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_sync_cards) {
            Intent intent = new Intent(ServerCardsListActivity.this, CardsSyncActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent(ServerCardsListActivity.this, ServerCardsListByThemeActivity.class);
        Bundle b = new Bundle();
        if (id == R.id.all_items) {
            Intent intentAll = new Intent(ServerCardsListActivity.this,ServerCardsListActivity.class);
            startActivity(intentAll);
        }
           else if (id == R.id.culture_art) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_CULTURE_ART); //Your id
            intent.putExtras(b);
            startActivity(intent);
        }
          else if (id == R.id.modern_technologies) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_MODERN_TECHNOLOGIES); //Your id
            intent.putExtras(b);
            startActivity(intent);
        }
        else if (id == R.id.society_politics) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_SOCIETY_POLITICS); //Your id
            intent.putExtras(b);
            startActivity(intent);
        } else if (id == R.id.adventure_travel) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_ADVENTURE_TRAVEL); //Your id
            intent.putExtras(b);
            startActivity(intent);
        } else if (id == R.id.nature_weather) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_NATURE_WEATHER); //Your id
            intent.putExtras(b);
            startActivity(intent);
        } else if (id == R.id.education_profession) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_EDUCATION_PROFESSION); //Your id
            intent.putExtras(b);
            startActivity(intent);
        } else if (id == R.id.appearance_character) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_APPEARANCE_CHARACTER); //Your id
            intent.putExtras(b);
            startActivity(intent);
        } else if (id == R.id.clothes_fashion) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_CLOTHES_FASHION); //Your id
            intent.putExtras(b);
            startActivity(intent);
        } else if (id == R.id.sport) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_SPORT); //Your id
            intent.putExtras(b);
            startActivity(intent);
        } else if (id == R.id.family_relationship) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_FAMILY_RELATIONSHIP); //Your id
            intent.putExtras(b);
            startActivity(intent);
        } else if (id == R.id.order_of_day) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_THE_ORDER_OF_DAY); //Your id
            intent.putExtras(b);
            startActivity(intent);
        } else if (id == R.id.hobbies_free_time) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_HOBBIES_FREE_TIME); //Your id
            intent.putExtras(b);
            startActivity(intent);
        } else if (id == R.id.customs_traditions) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_CUSTOMS_TRADITIONS); //Your id
            intent.putExtras(b);
            startActivity(intent);
        } else if (id == R.id.shopping) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_SHOPPING); //Your id
            intent.putExtras(b);
            startActivity(intent);
        } else if (id == R.id.food_drinks) {
            b.putString("card", CardDBSchema.CardTable.Themes.THEME_FOOD_DRINKS); //Your id
            intent.putExtras(b);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void addOnScrollListener(RecyclerView recyclerView, GridLayoutManager gridLayoutManager){
        recyclerView.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadAllCardsNextTime();}
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
}

