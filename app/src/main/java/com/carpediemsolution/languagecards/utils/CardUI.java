package com.carpediemsolution.languagecards.utils;

import android.text.InputFilter;

import com.carpediemsolution.languagecards.model.Card;
import com.carpediemsolution.languagecards.database.CardDBSchema;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Юлия on 27.04.2017.
 */

public class CardUI {

    public static InputFilter[] setSizeForCardEditText() {
        int maxLength = 40;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        return FilterArray;
    }

    public static InputFilter[] setSizeForUserEditText() {
        int maxLength = 15;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        return FilterArray;
    }

    public static InputFilter[] setSizeForCardDescriptionEditText() {
        int maxLength = 998;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        return FilterArray;
    }

    public static InputFilter[] setSizeForUserEmailEditText() {
        int maxLength = 35;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        return FilterArray;
    }

    public static String dialogMessage(Card card) {
        if (card.getDescription() == null) {
            return "There is no description for this card";
        } else return card.getDescription();
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static String returnTheme(Card card) {

        switch (card.getTheme()) {
            case (CardDBSchema.CardTable.Themes.THEME_CULTURE_ART):
                return Preferences.CULTURE_ART;
            case (CardDBSchema.CardTable.Themes.THEME_MODERN_TECHNOLOGIES):
                return Preferences.MODERN_TECHNOLOGIES;
            case (CardDBSchema.CardTable.Themes.THEME_SOCIETY_POLITICS):
                return Preferences.SOCIETY_POLITICS;
            case (CardDBSchema.CardTable.Themes.THEME_ADVENTURE_TRAVEL):
                return Preferences.ADVENTURE_TRAVEL;
            case (CardDBSchema.CardTable.Themes.THEME_NATURE_WEATHER):
                return Preferences.NATURE_WEATHER;
            case (CardDBSchema.CardTable.Themes.THEME_EDUCATION_PROFESSION):
                return Preferences.EDUCATION_PROFESSION;
            case (CardDBSchema.CardTable.Themes.THEME_APPEARANCE_CHARACTER):
                return Preferences.APPEARANCE_CHARACTER;
            case (CardDBSchema.CardTable.Themes.THEME_CLOTHES_FASHION):
                return Preferences.CLOTHES_FASHION;
            case (CardDBSchema.CardTable.Themes.THEME_SPORT):
                return Preferences.SPORT;
            case (CardDBSchema.CardTable.Themes.THEME_FAMILY_RELATIONSHIP):
                return Preferences.FAMILY_RELATIONSHIP;
            case (CardDBSchema.CardTable.Themes.THEME_THE_ORDER_OF_DAY):
                return Preferences.ORDER_OF_DAY;
            case (CardDBSchema.CardTable.Themes.THEME_HOBBIES_FREE_TIME):
                return Preferences.HOBBIES_FREE_TIME;
            case (CardDBSchema.CardTable.Themes.THEME_CUSTOMS_TRADITIONS):
                return Preferences.CUSTOMS_TRADITIONS;
            case (CardDBSchema.CardTable.Themes.THEME_SHOPPING):
                return Preferences.SHOPPING;
            case (CardDBSchema.CardTable.Themes.THEME_FOOD_DRINKS):
                return Preferences.FOOD_DRINKS;
            default:
                return Preferences.CULTURE_ART;
        }
    }
}


