package com.carpediemsolution.languagecards.views;

import com.carpediemsolution.languagecards.model.Card;

import java.util.List;

public interface MainView {
    void showCounters(List<Card> cards);

    void showLoading();

    void showEmpty();
}
