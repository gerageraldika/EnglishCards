package com.carpediemsolution.languagecards.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/**
 * Created by Юлия on 06.04.2017.
 */

public class CardReceiver extends BroadcastReceiver {

    private static final String TAG = "CardReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "BroadcastReceiver has received alarm intent.");

        context.startService(new Intent(context, CardService.class));

    }

}
