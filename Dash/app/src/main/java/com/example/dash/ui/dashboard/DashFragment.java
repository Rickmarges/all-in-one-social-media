package com.example.dash.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.dash.R;
import com.example.dash.ui.account.AccountActivity;

import java.util.ArrayList;
import java.util.List;

public class DashFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_dash, container, false);

        ImageButton addBtn = rootView.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), AccountActivity.class);
            startActivity(intent);
        });

        createUI();

        return rootView;
    }

    private void createUI(){
        List<CardView> allCards = new ArrayList<>();
        List<CardView> redditCards = new ArrayList<>();
        List<CardView> twitterCards = new ArrayList<>();

        redditCards.add(new CardView(getContext()));
        redditCards.add(new CardView(getContext()));
        redditCards.add(new CardView(getContext()));
        twitterCards.add(new CardView(getContext()));
        twitterCards.add(new CardView(getContext()));
        twitterCards.add(new CardView(getContext()));
        twitterCards.add(new CardView(getContext()));
        twitterCards.add(new CardView(getContext()));

//        redditCards = RedditFragment.getInstance().getCardList();
//        twitterCards = TwitterFragment.getInstance().getCardList();

        int i = 0;

        while(i < redditCards.size() || i < twitterCards.size()) {
            if(i < redditCards.size()) {
                allCards.add(redditCards.get(i));
            }
            if(i < twitterCards.size()) {
                allCards.add(twitterCards.get(i));
            }
            i++;
        }
        return;
    }
}
