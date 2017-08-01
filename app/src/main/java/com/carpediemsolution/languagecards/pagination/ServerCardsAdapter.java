package com.carpediemsolution.languagecards.pagination;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.dao.CardLab;
import com.carpediemsolution.languagecards.utils.CardUI;
import com.carpediemsolution.languagecards.R;
import com.carpediemsolution.languagecards.utils.Preferences;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Created by Юлия on 17.05.2017.
 */

public class ServerCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Card> cards;
    private Card mCard;

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context context;
    private boolean isLoadingAdded = false;

    private InterstitialAd interstitial;

    public void setCards(List<Card> mcards) {
        cards = mcards;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    public ServerCardsAdapter(Context context) {
        this.context = context;
        cards = new ArrayList<>();
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.recycler_view_item, parent, false);
        viewHolder = new ViewHolder(v1) {
        };
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Card card = cards.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final ViewHolder viewHolder = (ViewHolder) holder;
                if (card.getPerson_id() == 1) {
                    viewHolder.mWordTextView.setTextColor(Color.parseColor("#37474F"));
                    viewHolder.mWordTextView.setText(card.getWord());
                } else if (card.getPerson_id() == 0) {
                    viewHolder.mWordTextView.setText(card.getTranslate());
                    viewHolder.mWordTextView.setTextColor(Color.parseColor("#558B2F"));
                }

                //  Log.d(LOG_TAG, "---description " + card.getDescription());
                if (card.getDescription() != null) {
                    viewHolder.imageView.setImageResource(R.drawable.ic_action_description);
                    viewHolder.imageView.setVisibility(View.VISIBLE);
                } else viewHolder.imageView.setVisibility(View.INVISIBLE);

                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (cards == null)
            return 0;
        return cards.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == cards.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    private void add(Card card) {
        cards.add(card);
        // notifyItemInserted(cards.size() - 1);
        notifyItemInserted(cards.size());
    }

    public void addAll(List<Card> cards) {
        for (Card card : cards) {
            add(card);
        }
    }

    private void remove(Card card) {
        int position = cards.indexOf(card);
        if (position > -1) {
            cards.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Card());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = cards.size() - 1;

        Card card = getItem(position);

        if (card != null) {
            cards.remove(position);
            notifyItemRemoved(position);
        }
    }

    private Card getItem(int position) {
        return cards.get(position);
    }


    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView mWordTextView;
        ImageView imageView;

        private ViewHolder(View itemView) {
            super(itemView);
            mWordTextView = (TextView) itemView.findViewById(R.id.list_item__word_text_view);
            imageView = (ImageView) itemView.findViewById(R.id.image_for_description);
            imageView.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            mCard = cards.get(position);
            mWordTextView = (TextView) itemView.findViewById(R.id.list_item__word_text_view);

            if (mCard.getPerson_id() == 1) {
                mWordTextView.setText(mCard.getTranslate());
                mWordTextView.setTextColor(Color.parseColor("#558B2F"));
                mCard.setPerson_id(0);
            } else if (mCard.getPerson_id() == 0) {
                mWordTextView.setText(mCard.getWord());
                mWordTextView.setTextColor(Color.parseColor("#37474F"));
                mCard.setPerson_id(1);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            showCardDescription(position, context);
            return false;
        }
    }

    private class LoadingVH extends RecyclerView.ViewHolder {

        private LoadingVH(View itemView) {
            super(itemView);
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        interstitial.loadAd(adRequest);
    }

    private void addCard(Card mCard) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences
                (context);
        String token = prefs.getString(Preferences.TOKEN, "");
        if (token.equals("")) {
            token = prefs.getString(Preferences.ANON_TOKEN, "");
            if (token.equals("")) {
                token = "anonym " + new Date().toString();
                prefs.edit().putString(Preferences.ANON_TOKEN, token).apply();
            }
        }
        mCard.setId(String.valueOf(UUID.randomUUID()));
        CardLab CardrLab = CardLab.get(context);
        CardrLab.addCard(mCard);
    }


    private void showCardDescription(int position, Context context) {
        mCard = cards.get(position);
        CardUI cardUI = new CardUI();

        interstitial = new InterstitialAd(context);
        interstitial.setAdUnitId(context.getString(R.string.recycler_view_admob));

        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                addCard(mCard);
            }
        });

        requestNewInterstitial();

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyTheme_Dark_Dialog);
        String dialogMessage = cardUI.dialogMessage(mCard);
        builder.setTitle(mCard.getWord() + " ~ " + cardUI.returnTheme(mCard))
                .setMessage(mCard.getTranslate() + "\n\n" + dialogMessage)
                .setPositiveButton(context.getString(R.string.add_card), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (interstitial.isLoaded()) {
                            interstitial.show();
                        } else {
                            addCard(mCard);
                        }
                    }
                })
                .show();
    }
}