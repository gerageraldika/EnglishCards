package com.carpediemsolution.languagecards.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.carpediemsolution.languagecards.database.CardBaseHelper;
import com.carpediemsolution.languagecards.database.CardCursorWrapper;
import com.carpediemsolution.languagecards.database.CardDBSchema.CardTable;
import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Юлия on 21.03.2017.
 */

public class CardLab {

    private static CardLab sCardLab;

    private Context mContext;
    public SQLiteDatabase mDatabase;
    private static String LAB_LOG = "LabLog";

    public static CardLab get(Context context) {
        if (sCardLab == null) {
            sCardLab = new CardLab(context);
        }
        return sCardLab;
    }

    private CardLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CardBaseHelper(mContext)
                .getWritableDatabase();
    }


    public void addCard(Card c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(CardTable.NAME_ENRUS, null, values);
        Log.d(LAB_LOG, "---- addCard----" + values);
    }

    public void addCards(List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            ContentValues values = getContentValues(cards.get(i));
            mDatabase.insert(CardTable.NAME_ENRUS, null, values);
            Log.d(LAB_LOG, "---- addCard----" + values);
        }
    }

    public List<Card> getCards() {
        Cursor cursor1 = mDatabase.query(
                CardTable.NAME_ENRUS,
                null, // Columns - null selects all columns
                null,
                null,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        List<Card> cards = new ArrayList<>();

        CardCursorWrapper cursor = new CardCursorWrapper(cursor1);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            cards.add(cursor.getCard());
            cursor.moveToNext();
        }
        cursor.close();
        cursor1.close();
        // Collections.reverse(cards);
        Collections.shuffle(cards);
        Log.d(LAB_LOG, "---- getCards----" + cards);
        return cards;
    }

    public List<Card> getCardsByTheme(String theme) {

        String sSelect = "SELECT * FROM " + CardTable.NAME_ENRUS + " WHERE " + CardTable.Cols.THEME
                + " = '" + theme + "'";
        //Cursor cursor = mDb.rawQuery(sSelect, null);
        List<Card> cards = new ArrayList<>();
        try {
            Cursor cursor1 = mDatabase.rawQuery(sSelect, null);

            if (cursor1 != null) {

                CardCursorWrapper cursor = new CardCursorWrapper(cursor1);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    cards.add(cursor.getCard());
                    cursor.moveToNext();
                }
                cursor.close();
                cursor1.close();
                Log.d(LAB_LOG, "---- getCardsByTheme----" + cards);
            }
            Collections.reverse(cards);
        } catch (SQLiteException e) {
            Log.d(LAB_LOG, "sqlite e" + e.toString());
        } catch (Exception ex) {
            Log.d(LAB_LOG, "exception" + ex.toString());
        }
        Collections.shuffle(cards);
        return cards;
    }

    public void deleteAllCards() {
        mDatabase.execSQL("delete from " + CardTable.NAME_ENRUS);
    }

    public Card getCard(String id) {
        CardCursorWrapper cursor = queryCard(
                CardTable.Cols.UUID_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCard();
        } finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Card card) {
        ContentValues values = new ContentValues();
        values.put(CardTable.Cols.UUID_ID, card.getId());
        values.put(CardTable.Cols.ENRUS_PERSON_ID, card.getPerson_id());
        values.put(CardTable.Cols.ENRUS_WORD, card.getWord());
        values.put(CardTable.Cols.ENRUS_TRANSLATE, card.getTranslate());
        values.put(CardTable.Cols.ENRUS_DESCRIPTION, card.getDescription());
        values.put(CardTable.Cols.THEME, card.getTheme());
        Log.d(LAB_LOG, "----" + "ContentValues " + "----");
        return values;
    }

    private CardCursorWrapper queryCard(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CardTable.NAME_ENRUS,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );
        cursor.close();
        return new CardCursorWrapper(cursor);
    }

    public void updateCard(Card card) {
        String id = card.getId();
        ContentValues values = getContentValues(card);

        mDatabase.update(CardTable.NAME_ENRUS, values,
                CardTable.Cols.UUID_ID + " = ?",
                new String[]{id});
    }


    private static ContentValues getUserContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(CardTable.Cols.NAME, user.getUsername());
        return values;
    }

    public void addUser(User user) {
        ContentValues values = getUserContentValues(user);
        mDatabase.insert(CardTable.USER_NAME, null, values);
    }

    public User getUser() {
        String[] projection = {
                CardTable.Cols.NAME,
        };
        Cursor cursor = mDatabase.query(
                CardTable.USER_NAME,  // The table to query
                projection, // Columns - null selects all columns
                null,
                null,
                null, // groupBy
                null, // having
                null  // orderBy
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex(CardTable.Cols.NAME));

            User user = new User();
            user.setUsername(name);
            return user;
        } finally {
            cursor.close();
        }
    }

    public void deleteUser() {
        mDatabase.execSQL("delete from " + CardTable.USER_NAME);
    }
}
