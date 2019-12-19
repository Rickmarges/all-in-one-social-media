package com.example.dash.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.dash.R;
import com.example.dash.ui.account.AccountActivity;

import java.util.ArrayList;
import java.util.List;

public class DashFragment extends Fragment {
    private static DashFragment instance;
    private List<CardView> redditCards = new ArrayList<>();
    private List<CardView> twitterCards = new ArrayList<>();
    private LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_dash, container, false);

        linearLayout = rootView.findViewById(R.id.dashLayout);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        createUI();
        instance = this;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        linearLayout.removeAllViews();
    }

    void setRedditCards(List<CardView> redditCards) {
        this.redditCards = redditCards;
        createUI();
    }

    public void setTwitterCards(List<CardView> twitterCards) {
        this.twitterCards = twitterCards;
    }

    private void createUI() {
        List<CardView> allCards = new ArrayList<>();

        if (linearLayout.getChildCount() > 0) {
            linearLayout.removeAllViews();
            allCards.clear();
        }

        if (redditCards.size() == 0 && twitterCards.size() == 0) {
            return;
        }

        int i = 0;

        while (i < redditCards.size() || i < twitterCards.size()) {
            if (i < redditCards.size()) {
                allCards.add(redditCards.get(i));
            }
            if (i < twitterCards.size()) {
                allCards.add(twitterCards.get(i));
            }
            i++;
        }

        for (CardView cardView : allCards) {
            linearLayout.addView(cardView);
        }
    }

    static DashFragment getInstance() {
        return instance;
    }
}
