package com.example.dash.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dash.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class DashFragment extends Fragment {
    private static DashFragment instance;
    private List<CardView> redditCards = new ArrayList<>();
    private List<CardView> twitterCards = new ArrayList<>();
    private LinearLayout linearLayout;
    private SwipeRefreshLayout swipeLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_dash, container, false);

        linearLayout = rootView.findViewById(R.id.dashLayout);

        swipeLayout = rootView.findViewById(R.id.swipe);
        swipeLayout.setOnRefreshListener(this::updateCards);

        // Change colours of bar and background to match style
        swipeLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackgroundPrimary);

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

    private void updateCards() {
        TwitterFragment twitterFragment;
        RedditFragment.getInstance().updateReddit();
        if (TwitterFragment.getInstance() == null) {
            twitterFragment = new TwitterFragment();
        } else {
            twitterFragment = TwitterFragment.getInstance();
        }
        twitterFragment.update();
    }

    void setRedditCards(List<CardView> redditCards) {
        this.redditCards = redditCards;
        createUI();
    }

    void setTwitterCards(List<CardView> twitterCards) {
        this.twitterCards = twitterCards;
        createUI();
    }

    private void createUI() {
        List<CardView> allCards = new ArrayList<>();

        if (linearLayout == null) {
            linearLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.dashLayout);
        }

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
        swipeLayout.setRefreshing(false);
    }

    static DashFragment getInstance() {
        return instance;
    }
}
