package com.carpediemsolution.languagecards.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.carpediemsolution.languagecards.database.CardDBSchema.CardTable;

/**
 * Created by Юлия on 21.03.2017.
 */

public class CardBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "cardBase.db";

    public CardBaseHelper(Context context) {

        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + CardTable.NAME_ENRUS + "(" +
                " _id integer primary key autoincrement, " +
                CardTable.Cols.UUID_ID + ", " +
                CardTable.Cols.ENRUS_PERSON_ID + ", " +
                CardTable.Cols.ENRUS_WORD + ", " +
                CardTable.Cols.ENRUS_TRANSLATE + ", " +
                CardTable.Cols.ENRUS_DESCRIPTION + ", " +
                CardTable.Cols.THEME +
                ")"
        );

        db.execSQL("create table " + CardTable.USER_NAME + "(" +
                " _id integer primary key autoincrement, " +
                CardTable.Cols.NAME +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
