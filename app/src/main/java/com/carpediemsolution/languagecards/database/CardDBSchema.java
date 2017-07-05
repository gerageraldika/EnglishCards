package com.carpediemsolution.languagecards.database;

/**
 * Created by Юлия on 21.03.2017.
 */

public class CardDBSchema {

    public static final class CardTable {
        public static final String NAME_ENRUS= "enrus_card";
        public static final String USER_NAME= "user";

        public static final class Cols {
            public static final String UUID_ID = "uuid_id";
            public static final String ENRUS_PERSON_ID = "id";
            public static final String ENRUS_WORD = "word";
            public static final String ENRUS_TRANSLATE = "transl";
            public static final String ENRUS_DESCRIPTION = "desc";
            public static final String THEME = "theme";

            public static final String NAME = "name";
        }

        public static final class Themes {
            public static final String THEME_CULTURE_ART = "art";
            public static final String THEME_MODERN_TECHNOLOGIES = "tech";
            public static final String THEME_SOCIETY_POLITICS = "pol";
            public static final String THEME_ADVENTURE_TRAVEL = "trav";
            public static final String THEME_NATURE_WEATHER = "nat";
            public static final String THEME_EDUCATION_PROFESSION = "edu";
            public static final String THEME_APPEARANCE_CHARACTER = "app";
            public static final String THEME_CLOTHES_FASHION = "cl";
            public static final String THEME_SPORT = "sp";
            public static final String THEME_FAMILY_RELATIONSHIP = "fam";
            public static final String THEME_THE_ORDER_OF_DAY = "them";
            public static final String THEME_HOBBIES_FREE_TIME = "hob";
            public static final String THEME_CUSTOMS_TRADITIONS = "cust";
            public static final String THEME_SHOPPING = "shop";
            public static final String THEME_FOOD_DRINKS = "food";
        }

    }
}