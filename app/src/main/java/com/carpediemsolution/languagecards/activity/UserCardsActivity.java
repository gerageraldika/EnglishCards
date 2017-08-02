package com.carpediemsolution.languagecards.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.carpediemsolution.languagecards.App;
import com.carpediemsolution.languagecards.api.WebApi;
import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.dao.CardLab;
import com.carpediemsolution.languagecards.utils.CardUI;
import com.carpediemsolution.languagecards.R;
import com.carpediemsolution.languagecards.database.CardDBSchema;
import com.carpediemsolution.languagecards.notification.CardReceiver;
import com.carpediemsolution.languagecards.utils.Preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserCardsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = "MainActivity";
    private static final String TAG = "Notification";

    @BindView(R.id.card_recycler_view)
    RecyclerView cardRecyclerView;
    private CardAdapter mAdapter;
    private List<Card> mCards;
    private Card mCard;
    private CardLab cardsLab;
    private CardUI cardUI;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @OnClick(R.id.fab)
    public void onClick() {
        Intent intent = new Intent(UserCardsActivity.this, InsertNewCardActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor(getString(R.string.color_primary)));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nest_scrollview_main);
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });

        cardsLab = CardLab.get();
        mCards = cardsLab.getCards();
        cardRecyclerView.setLayoutManager(new GridLayoutManager(UserCardsActivity.this, 3));
        updateUI();

        cardUI = new CardUI();

        setAlarm();
    }

    private void updateUI() {
        mAdapter = new CardAdapter();
        cardRecyclerView.setAdapter(mAdapter);
        mAdapter.setCards(mCards);
        mAdapter.notifyDataSetChanged();
    }

   /* @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Card mCardItem;
        private List<Card> mCardItems = new ArrayList<>();
        private static final int EMPTY_VIEW = 10;

        private TextView mWordTextView;
        private ImageView imageView;

        private class EmptyViewHolder extends RecyclerView.ViewHolder {
            private EmptyViewHolder(View itemView) {
                super(itemView);
                Log.d(LOG_TAG, "----" + "empty holder view");
            }
        }

        private class CardHolder extends RecyclerView.ViewHolder
                implements View.OnLongClickListener, View.OnClickListener {

            private CardHolder(View itemView) {
                super(itemView);
                itemView.setOnLongClickListener(this);
                itemView.setOnClickListener(this);
                mWordTextView = (TextView) itemView.findViewById(R.id.list_item__word_text_view);
                imageView = (ImageView) itemView.findViewById(R.id.image_for_description);
            }

            @Override
            public boolean onLongClick(View v) {
                int position = getAdapterPosition();
                mCardItem = mCardItems.get(position);
                Log.d(LOG_TAG, "----" + "holder to delete");
                deleteCard(position);
                return false;
            }

            @Override
            public void onClick(View v) {
                int position = getLayoutPosition();
                mCardItem = mCardItems.get(position);
                mWordTextView = (TextView) itemView.findViewById(R.id.list_item__word_text_view);

                if (mWordTextView.getText().equals(mCardItem.getWord())) {
                    mWordTextView.setText(mCardItem.getTranslate());
                    mWordTextView.setTextColor(Color.parseColor(getString(R.string.color_primary)));
                } else if (mWordTextView.getText().equals(mCardItem.getTranslate())) {
                    mWordTextView.setText(mCardItem.getWord());
                    mWordTextView.setTextColor(Color.parseColor(getString(R.string.color_accent)));
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mCardItems.size() == 0) {
                return EMPTY_VIEW;
            } else {
                return super.getItemViewType(position);
            }
        }

        public void setCards(List<Card> cards) {
            mCardItems = cards;
        }

        @Override
        public int getItemCount() {
            return mCardItems.size() > 0 ? mCardItems.size() : 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;

            if (viewType == EMPTY_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view, parent, false);
                EmptyViewHolder evh = new EmptyViewHolder(v);
                Log.d(LOG_TAG, "----" + "onCreateViewHolder" + mCardItems.size());
                return evh;
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
                CardHolder vh = new CardHolder(v);
                Log.d(LOG_TAG, "----" + "onCreateViewHolder" + mCardItems.size());
                return vh;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CardHolder) {
                CardHolder vh = (CardHolder) holder;
                mCardItem = mCardItems.get(position);
                Log.d(LOG_TAG, "----" + "onCreateHolder" + mCardItem.getWord());
                mWordTextView.setText(mCardItem.getWord());
                if (mCardItem.getDescription() != null) {
                    if (!mCardItem.getDescription().equals(""))
                        imageView.setImageResource(R.drawable.ic_action_description);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cards_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_stock: {
                Intent intent = new Intent(UserCardsActivity.this, ServerCardsActivity.class);
                startActivity(intent);
                return  true;
            }
            case R.id.action_my_cards: {
                Intent intent = new Intent(UserCardsActivity.this, UserCardsActivity.class);
                startActivity(intent);
                return  true;
            }

            case R.id.action_line: {
                cardRecyclerView.setLayoutManager(new GridLayoutManager(UserCardsActivity.this, 1));
                updateUI();
                return  true;
            }
            case R.id.action_frame: {
                cardRecyclerView.setLayoutManager(new GridLayoutManager(UserCardsActivity.this, 3));
                updateUI();
                return  true;
            }
            case R.id.action_settings: {

                SharedPreferences prefs = PreferenceManager.
                        getDefaultSharedPreferences(UserCardsActivity.this);
                String token = prefs.getString(Preferences.TOKEN, "");
                Log.d(LOG_TAG, "---token " + token);
                if (token.equals("")) {
                    Intent intent = new Intent(UserCardsActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(UserCardsActivity.this, UserAuthorizedActivity.class);
                    startActivity(intent);
                }
                return  true;
            }
            case R.id.action_about_app: {
                Intent intent = new Intent(UserCardsActivity.this, InformationActivity.class);
                startActivity(intent);
                return  true;
            }
            case R.id.action_sync_cards: {
                Intent intent = new Intent(UserCardsActivity.this, CardsSyncActivity.class);
                startActivity(intent);
                return  true;
            }
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        CardLab calcLab = CardLab.get();
        switch (item.getItemId()) {
            case (R.id.culture_art): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_CULTURE_ART);
                updateUI();
                break;
            }
            case (R.id.modern_technologies): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_MODERN_TECHNOLOGIES);
                updateUI();
                break;
            }
            case (R.id.society_politics): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_SOCIETY_POLITICS);
                updateUI();
                break;
            }
            case (R.id.adventure_travel): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_ADVENTURE_TRAVEL);
                updateUI();
                break;
            }
            case (R.id.nature_weather): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_NATURE_WEATHER);
                updateUI();
                break;
            }
            case (R.id.education_profession): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_EDUCATION_PROFESSION);
                updateUI();
                break;
            }
            case (R.id.appearance_character): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_APPEARANCE_CHARACTER);
                updateUI();
                break;
            }
            case (R.id.clothes_fashion): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_CLOTHES_FASHION);
                updateUI();
                break;
            }
            case (R.id.sport): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_SPORT);
                updateUI();
                break;
            }
            case (R.id.family_relationship): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_FAMILY_RELATIONSHIP);
                updateUI();
                break;
            }
            case (R.id.order_of_day): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_THE_ORDER_OF_DAY);
                updateUI();
                break;
            }
            case (R.id.hobbies_free_time): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_HOBBIES_FREE_TIME);
                updateUI();
                break;
            }
            case (R.id.customs_traditions): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_CUSTOMS_TRADITIONS);
                updateUI();
                break;
            }
            case (R.id.shopping): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_SHOPPING);
                updateUI();
                break;
            }
            case (R.id.food_drinks): {
                mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_FOOD_DRINKS);
                updateUI();
                break;
            }
            case (R.id.all_items): {
                mCards = calcLab.getCards();
                updateUI();
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void deleteCard(final int position) {
        mCard = mCards.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(UserCardsActivity.this, R.style.MyTheme_Dark_Dialog);
        builder.setTitle(mCard.getWord() + " ~ " + cardUI.returnTheme(mCard));
        String dialogMessage = cardUI.dialogMessage(mCard);
        builder.setMessage(mCard.getTranslate() + "\n\n" + dialogMessage);
        builder.setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openDeleteDialog(position);
            }
        }).setNegativeButton(getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(UserCardsActivity.this, EditCardActivity.class);
                Bundle b = new Bundle();
                b.putString("card", mCard.getId()); //Your id
                intent.putExtras(b);
                startActivity(intent);
            }
        }).show();
    }

    protected void openDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserCardsActivity.this, R.style.MyTheme_Dark_Dialog);
        builder.setMessage(getString(R.string.are_you_sure));
        builder.setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mAdapter.notifyItemRemoved(position);//item removed from recylcerview
                mCard = mCards.get(position);

                final String uuidString = mCard.getId();
                final WebApi webApi = App.getWebApi();

                mCards.remove(position);

                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences
                        (UserCardsActivity.this);
                String token = prefs.getString(Preferences.TOKEN, "");
                if (token.equals("")) {
                    token = prefs.getString(Preferences.ANON_TOKEN, "");
                    if (token.equals("")) {
                        cardsLab.mDatabase.delete(CardDBSchema.CardTable.NAME_ENRUS,
                                CardDBSchema.CardTable.Cols.UUID_ID + " = '" + uuidString + "'", null);
                    }
                }
                Call<ResponseBody> callPost = webApi.deleteCard(token, mCard);
                callPost.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String s = response.body().string();
                                Log.d(LOG_TAG, "---token " + s);
                                if (s.equals(Preferences.CARD_DELETED) || s.equals(Preferences.NO_CARDS__EXIST)) {
                                    cardsLab.mDatabase.delete(CardDBSchema.CardTable.NAME_ENRUS,
                                            CardDBSchema.CardTable.Cols.UUID_ID + " = '" + uuidString + "'", null);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        String anontoken = prefs.getString(Preferences.TOKEN, "");
                        Log.d(LOG_TAG, "---anontoken " + anontoken);
                        if (anontoken.equals("")) {
                            anontoken = prefs.getString(Preferences.ANON_TOKEN, "");
                            Log.d(LOG_TAG, "---anontoken " + anontoken);
                            if (anontoken.equals("")) {
                                cardsLab.mDatabase.delete(CardDBSchema.CardTable.NAME_ENRUS,
                                        CardDBSchema.CardTable.Cols.UUID_ID + " = '" + uuidString + "'", null);
                            }
                        } else {
                            Toast.makeText(UserCardsActivity.this, R.string.delete_cancel,
                                    Toast.LENGTH_SHORT).show();
                            mCard = cardsLab.getCard(uuidString);
                            mCards.add(mCard);
                        }
                        updateUI();
                    }
                });
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(getApplicationContext(), CardReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar alarmStartTime = Calendar.getInstance();

        alarmStartTime.set(Calendar.HOUR_OF_DAY, 18);
        alarmStartTime.set(Calendar.MINUTE, 10);
        alarmStartTime.set(Calendar.SECOND, 15);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.i(TAG, "Alarms set every day");
    }
}




