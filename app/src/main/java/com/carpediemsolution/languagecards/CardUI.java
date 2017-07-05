package com.carpediemsolution.languagecards;

import android.text.InputFilter;

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
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isDescriptionExist(String email) {
        boolean isValid = false;

       // String expression = "*[[][]]+@[A-Z,А-Я]{1,}$";
        CharSequence inputStr = email;

       // Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
      //  Matcher matcher = pattern.matcher(inputStr);
      //  if (matcher.matches()) {
      //      isValid = true;
       // }
        return isValid;
    }

    public static String returnTheme(Card card) {

        String theme = "";
        if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_CULTURE_ART))
            theme = "Culture and Art";
        else if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_MODERN_TECHNOLOGIES))
            theme = "Modern technologies";
        else if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_SOCIETY_POLITICS))
            theme = "Society and Politics";
        else if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_ADVENTURE_TRAVEL))
            theme = "Adventure travel";
        else if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_NATURE_WEATHER))
            theme = "Nature and Weather";
        else if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_EDUCATION_PROFESSION))
            theme = "Education and Profession";
        else if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_APPEARANCE_CHARACTER))
            theme = "Appearance and Character";
        else if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_CLOTHES_FASHION))
            theme = "Clothes and Fashion";
        else if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_SPORT))
            theme = "Sport";
        else if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_FAMILY_RELATIONSHIP))
            theme = "Family and Relationship";
        else if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_THE_ORDER_OF_DAY))
            theme = "The order of the day";
        else if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_HOBBIES_FREE_TIME))
            theme = "Hobbies and Free time";
        else if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_CUSTOMS_TRADITIONS))
            theme = "Customs and Traditions";
        else if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_SHOPPING))
            theme = "Shopping";
        else if (card.getTheme().equals(CardDBSchema.CardTable.Themes.THEME_FOOD_DRINKS))
            theme = "Food and Drinks";
        return theme;
        }

    }


