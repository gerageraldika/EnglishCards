package com.carpediemsolution.languagecards.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.carpediemsolution.languagecards.R;


/**
 * Created by Юлия on 04.05.2017.
 */

public class InformationActivity extends Activity {

    private TextView infoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        infoTextView = (TextView) findViewById(R.id.info_textview);

        infoTextView.setText(getString(R.string.info));
    }
}
