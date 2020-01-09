package com.dash.Fragments;

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

import com.dash.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DashFragment extends Fragment {
    private static DashFragment sInstance;
    private List<CardView> mRedditCardList = new ArrayList<>();
    private List<CardView> mTwitterCardList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mLinearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_dash, container, false);

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe);
        mSwipeRefreshLayout.setOnRefreshListener(this::updateCards);

        // Change colours of bar and background to match style
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackgroundPrimary);

        mLinearLayout = Objects.requireNonNull(rootView.findViewById(R.id.dashLayout));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        sInstance = this;
        mRedditCardList.clear();
        mTwitterCardList.clear();
    }

    private void updateCards() {
        RedditFragment redditFragment = RedditFragment.getInstance();
        if (redditFragment == null) {
            redditFragment = new RedditFragment();
        }
        redditFragment.updateReddit();

        TwitterFragment twitterFragment = TwitterFragment.getInstance();
        if (twitterFragment == null) {
            twitterFragment = new TwitterFragment();
        }
        twitterFragment.updateTwitter();
    }

    void setRedditCards(List<CardView> redditCards) {
        this.mRedditCardList = redditCards;
        createUI();
    }

    void setTwitterCards(List<CardView> twitterCards) {
        this.mTwitterCardList = twitterCards;
        createUI();
    }

    private void createUI() {
        List<CardView> cardViewList = new ArrayList<>();

        if (mLinearLayout != null) {
            if (mLinearLayout.getChildCount() > 0) {
                mLinearLayout.removeAllViews();
            }
        }

        if (mRedditCardList.size() == 0 && mTwitterCardList.size() == 0) {
            return;
        }

        int i = 0;

        while (i < mRedditCardList.size() || i < mTwitterCardList.size()) {
            if (i < mRedditCardList.size()) {
                cardViewList.add(mRedditCardList.get(i));
            }
            if (i < mTwitterCardList.size()) {
                cardViewList.add(mTwitterCardList.get(i));
            }
            i++;
        }

        for (CardView cardView : cardViewList) {
            if (cardView.getParent() == null) {
                Objects.requireNonNull(mLinearLayout).addView(cardView);
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    static DashFragment getInstance() {
        return sInstance;
    }
}
