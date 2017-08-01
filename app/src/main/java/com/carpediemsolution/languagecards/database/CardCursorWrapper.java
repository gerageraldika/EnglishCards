package com.carpediemsolution.languagecards.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.carpediemsolution.languagecards.database.CardDBSchema.CardTable;
import com.carpediemsolution.languagecards.model.Card;


/**
 * Created by Юлия on 21.03.2017.
 */

public class CardCursorWrapper extends CursorWrapper {

    public CardCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Card getCard() {
        String uuidIdString = getString(getColumnIndex(CardTable.Cols.UUID_ID));
        String personIdString = getString(getColumnIndex(CardTable.Cols.ENRUS_PERSON_ID));
        String word = getString(getColumnIndex(CardTable.Cols.ENRUS_WORD));
        String translate = getString(getColumnIndex(CardTable.Cols.ENRUS_TRANSLATE));
        String description = getString(getColumnIndex(CardTable.Cols.ENRUS_DESCRIPTION));
        String theme = getString(getColumnIndex(CardTable.Cols.THEME));

        Card cardDB = new Card();
        cardDB.setId(uuidIdString);
        cardDB.setPerson_id(Integer.parseInt(personIdString));
        cardDB.setWord(word);
        cardDB.setTranslate(translate);
        cardDB.setDescription(description);
        cardDB.setTheme(theme);

        return cardDB;
    }
}
