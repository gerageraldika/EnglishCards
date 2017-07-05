package com.carpediemsolution.languagecards;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.carpediemsolution.languagecards.api.UserCardToDeleteAPI;
import com.carpediemsolution.languagecards.database.CardDBSchema;
import com.carpediemsolution.languagecards.notification.CardReceiver;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class CardsMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = "MainActivity";
    private static final String TAG = "Notification";
    private CardAdapter mAdapter;
    private RecyclerView mCardRecyclerView;
    public AlarmManager alarmManager;
    Intent alarmIntent;
    PendingIntent pendingIntent;
    List<Card> mCards;
    Card mCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#558B2F"));
        setSupportActionBar(toolbar);
        CardLab cardrLab = CardLab.get(CardsMainActivity.this);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CardsMainActivity.this, InsertNewCardActivity.class);
                startActivity(intent);
            }
        });

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

        mCards = cardrLab.getCards();

        mCardRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        mCardRecyclerView.setLayoutManager(new GridLayoutManager(CardsMainActivity.this, 3));
        updateUI();

       setAlarm();
    }

    private void updateUI() {
        mAdapter = new CardAdapter();

        mCardRecyclerView.setAdapter(mAdapter);
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
        CardLab cardrLab = CardLab.get(CardsMainActivity.this);
        List<Card> mCardItems = cardrLab.getCards();
        private static final int EMPTY_VIEW = 10;

        private TextView mWordTextView;
        ImageView imageView;

        public class EmptyViewHolder extends RecyclerView.ViewHolder {
            public EmptyViewHolder(View itemView) {
                super(itemView);
                Log.d(LOG_TAG, "----" + "empty holder view");
            }
        }

        private class CardHolder extends RecyclerView.ViewHolder
                implements View.OnLongClickListener, View.OnClickListener {

            public CardHolder(View itemView) {
                super(itemView);
                itemView.setOnLongClickListener(this);
                itemView.setOnClickListener(this);
                mWordTextView = (TextView) itemView.findViewById(R.id.list_item__word_text_view);
                imageView = (ImageView)itemView.findViewById(R.id.image_for_description);
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

                if(mWordTextView.getText().equals(mCardItem.getWord())){
                    mWordTextView.setText(mCardItem.getTranslate());
                    mWordTextView.setTextColor(Color.parseColor("#558B2F"));
                }
                else if(mWordTextView.getText().equals(mCardItem.getTranslate())){
                    mWordTextView.setText(mCardItem.getWord());
                    mWordTextView.setTextColor(Color.parseColor("#37474F"));
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
                if(mCardItem.getDescription() != null){
                    if(!mCardItem.getDescription().equals(""))
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
        int id = item.getItemId();

        if (id == R.id.action_stock) {
            Intent intent = new Intent(CardsMainActivity.this,ServerCardsListActivity.class);
            startActivity(intent);
        }
        if(id == R.id.action_my_cards){
            Intent intent = new Intent(CardsMainActivity.this,CardsMainActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_line) {
            mCardRecyclerView.setLayoutManager(new GridLayoutManager(CardsMainActivity.this, 1));
            updateUI();
        }
        if (id == R.id.action_frame) {
            mCardRecyclerView.setLayoutManager(new GridLayoutManager(CardsMainActivity.this, 3));
            updateUI();
        }
        if (id == R.id.action_settings) {

            SharedPreferences prefs = PreferenceManager.
                    getDefaultSharedPreferences(CardsMainActivity.this);
            String token = prefs.getString("Token", "");
            Log.d(LOG_TAG, "---token " + token);
            if (token == "") {
                Intent intent = new Intent(CardsMainActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(CardsMainActivity.this, AuthorizedPersonActivity.class);
                startActivity(intent);
            }
            return true;
        }
        if (id == R.id.action_about_app) {
            Intent intent = new Intent(CardsMainActivity.this, InformationActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_sync_cards) {
            Intent intent = new Intent(CardsMainActivity.this, CardsSyncActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        CardLab calcLab = CardLab.get(this);

        if (id == R.id.culture_art) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_CULTURE_ART);
            updateUI();
        } else if (id == R.id.modern_technologies) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_MODERN_TECHNOLOGIES);
            updateUI();
        } else if (id == R.id.society_politics) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_SOCIETY_POLITICS);
            updateUI();
        } else if (id == R.id.adventure_travel) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_ADVENTURE_TRAVEL);
            updateUI();
        } else if (id == R.id.nature_weather) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_NATURE_WEATHER);
            updateUI();
        } else if (id == R.id.education_profession) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_EDUCATION_PROFESSION);
            updateUI();
        } else if (id == R.id.appearance_character) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_APPEARANCE_CHARACTER);
            updateUI();
        } else if (id == R.id.clothes_fashion) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_CLOTHES_FASHION);
            updateUI();
        } else if (id == R.id.sport) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_SPORT);
            updateUI();
        } else if (id == R.id.family_relationship) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_FAMILY_RELATIONSHIP);
            updateUI();
        } else if (id == R.id.order_of_day) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_THE_ORDER_OF_DAY);
            updateUI();
        } else if (id == R.id.hobbies_free_time) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_HOBBIES_FREE_TIME);
            updateUI();
        } else if (id == R.id.customs_traditions) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_CUSTOMS_TRADITIONS);
            updateUI();
        } else if (id == R.id.shopping) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_SHOPPING);
            updateUI();
        } else if (id == R.id.food_drinks) {
            mCards = calcLab.getCardsByTheme(CardDBSchema.CardTable.Themes.THEME_FOOD_DRINKS);
            updateUI();
        } else if (id == R.id.all_items) {
            mCards = calcLab.getCards();
            updateUI();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void deleteCard(final int position) {
        mCard = mCards.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(CardsMainActivity.this,R.style.MyTheme_Dark_Dialog);
        builder.setTitle(mCard.getWord()+ " ~ " + CardUI.returnTheme(mCard));
        String dialogMessage = CardUI.dialogMessage(mCard);
        builder.setMessage(mCard.getTranslate()+ "\n\n" +dialogMessage);
        builder.setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openDeleteDialog(position);
                return;
            }
        }).setNegativeButton(getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CardsMainActivity.this, EditCardActivity.class);
                Bundle b = new Bundle();
                b.putString("card", mCard.getId()); //Your id
                intent.putExtras(b);
                startActivity(intent);
                return;
            }
        }).show();
    }

    protected void openDeleteDialog(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(CardsMainActivity.this,R.style.MyTheme_Dark_Dialog);
        builder.setMessage("Вы уверены?");
        builder.setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mAdapter.notifyItemRemoved(position);//item removed from recylcerview
                mCard = mCards.get(position);

                String uuidString = mCard.getId();
                CardLab CardrLab = CardLab.get(CardsMainActivity.this);

                Retrofit client = CardLab.get(CardsMainActivity.this).getRetfofitClient();
                mCards.remove(position);
                UserCardToDeleteAPI serviceUpload = client.create(UserCardToDeleteAPI.class);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences
                        (CardsMainActivity.this);
                String token = prefs.getString("Token", "");
                if (token == "") {
                    token = prefs.getString("AnonToken", "");
                    if (token == "") {
                        CardrLab.get(CardsMainActivity.this).mDatabase.delete(CardDBSchema.CardTable.NAME_ENRUS,
                                CardDBSchema.CardTable.Cols.UUID_ID + " = '" + uuidString + "'", null);
                        return;
                    }
                }
                        Call<ResponseBody> callPost = serviceUpload.deleteCard(token, mCard);
                        callPost.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    try {
                                        String s = response.body().string();
                                        Log.d(LOG_TAG, "---token " + s);
                                        if (s.equals("card deleted")||s.equals("no card exists")) {
                                            CardrLab.get(CardsMainActivity.this).mDatabase.delete(CardDBSchema.CardTable.NAME_ENRUS,
                                                    CardDBSchema.CardTable.Cols.UUID_ID + " = '" + uuidString + "'", null);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {


                                String anontoken = prefs.getString("Token", "");
                                Log.d(LOG_TAG, "---anontoken " + anontoken);
                                if (anontoken == "") {
                                    anontoken = prefs.getString("AnonToken", "");
                                    Log.d(LOG_TAG, "---anontoken " + anontoken);
                                    if (anontoken == "") {
                                        CardrLab.get(CardsMainActivity.this).mDatabase.delete(CardDBSchema.CardTable.NAME_ENRUS,
                                                CardDBSchema.CardTable.Cols.UUID_ID + " = '" + uuidString + "'", null);
                                    }
                                } else {
                                    Toast.makeText(CardsMainActivity.this, R.string.delete_cancel,
                                            Toast.LENGTH_SHORT).show();
                                    mCard = CardrLab.getCard(uuidString);
                                    mCards.add(mCard);
                                }
                                updateUI();
                            }
                        });
                return;
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        }).show();
    }

    public void setAlarm() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmIntent = new Intent(getApplicationContext(), CardReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, alarmIntent, pendingIntent.FLAG_UPDATE_CURRENT);

        Calendar alarmStartTime = Calendar.getInstance();

        alarmStartTime.set(Calendar.HOUR_OF_DAY, 18);
        alarmStartTime.set(Calendar.MINUTE, 10);
        alarmStartTime.set(Calendar.SECOND, 15);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.i(TAG, "Alarms set every day");
    }
}




