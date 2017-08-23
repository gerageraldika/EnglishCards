package com.carpediemsolution.languagecards.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Юлия on 21.03.2017.
 */

public class Card {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("person_id")
    @Expose
    private int person_id;
    @SerializedName("theme")
    @Expose
    private String theme;
    @SerializedName("translate")
    @Expose
    private String translate;
    @SerializedName("word")
    @Expose
    private String word;
    @SerializedName("description")
    @Expose
    private String description;

    public Card() {

    }

    public Card(int id){
        this.person_id = id;
    }

    public String getId() {
        return id;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public String getTheme() {return theme;}

    public void setTheme(String mTheme) {this.theme = mTheme;}

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String mTranslate) {
        this.translate = mTranslate;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String mWord) {
        this.word = mWord;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;

        Card card = (Card) o;

        if (getPerson_id() != card.getPerson_id()) return false;
        if (!getId().equals(card.getId())) return false;
        if (!getTheme().equals(card.getTheme())) return false;
        if (!getTranslate().equals(card.getTranslate())) return false;
        if (!getWord().equals(card.getWord())) return false;

        return getDescription().equals(card.getDescription());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getPerson_id();
        result = 31 * result + getTheme().hashCode();
        result = 31 * result + getTranslate().hashCode();
        result = 31 * result + getWord().hashCode();
        result = 31 * result + getDescription().hashCode();
        return result;
    }
}
