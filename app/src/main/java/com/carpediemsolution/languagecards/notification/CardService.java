package com.carpediemsolution.languagecards.notification;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.dao.CardLab;
import com.carpediemsolution.languagecards.activity.CardsMainActivity;
import com.carpediemsolution.languagecards.R;

import java.util.List;

/**
 * Created by Юлия on 06.04.2017.
 */

public class CardService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "CardService";
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;

    public CardService() {
        super("AlarmService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG,"Alarm Service has started.");
        Context context = this.getApplicationContext();
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, CardsMainActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources res = this.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        String notificationWords = getNotificationCard();

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_action_alarm)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.icon))
                .setTicker("Language Cards")
                .setAutoCancel(true)
                .setContentTitle("Do you know how to translate ")
                .setContentText(notificationWords);

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        Log.i(TAG,"Notifications sent.");

    }

    public String getNotificationCard(){

        List<Card> notificationCards = CardLab.get(getApplicationContext()).getCards();

        String notificationWords ="";

        if (notificationCards.size() == 0){
            notificationWords = " twinkle";
        }
        for (Card card: notificationCards){
            card.getWord();
            notificationWords = " " +card.getWord();
        }
        return notificationWords;
    }
}
