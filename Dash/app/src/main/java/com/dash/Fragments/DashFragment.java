/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Furthermore this project is licensed under the firebase.google.com/terms and
 * firebase.google.com/terms/crashlytics.
 *
 */

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

/**
 * Creates and fills the tablayout's leftmost tab
 */
public class DashFragment extends Fragment {
    private LinearLayout mLinearLayout;
    private List<CardView> mRedditCardList = new ArrayList<>();
    private List<CardView> mTwitterCardList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static DashFragment sInstance;

    /**
     * Create the View for dashboard
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     *                           The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        sInstance = this;
        mRedditCardList.clear();
        mTwitterCardList.clear();
    }

    /**
     * Update the instances of both Reddit and Twitter fragments
     */
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

    /**
     * Fills a list with cardViews with the Reddit Frontpage
     *
     * @param redditCards
     */
    void setRedditCards(List<CardView> redditCards) {
        this.mRedditCardList = redditCards;
        createUI();
    }

    /**
     * @param twitterCards
     */
    void setTwitterCards(List<CardView> twitterCards) {
        this.mTwitterCardList = twitterCards;
        createUI();
    }

    /**
     * Merge the two cardViewLists you get from Reddit and Twitter fragments.
     */
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
