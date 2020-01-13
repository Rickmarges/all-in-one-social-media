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

public class DashFragment extends Fragment {
    private static DashFragment sInstance;
    private List<CardView> mRedditCardList = new ArrayList<>();
    private List<CardView> mTwitterCardList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mLinearLayout;
    private Boolean mRedditReady;
    private Boolean mTwitterReady;

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

    @Override
    public void onResume() {
        super.onResume();
        sInstance = this;
        mRedditCardList.clear();
        mTwitterCardList.clear();
        updateCards();
    }

    private void updateCards() {
        mRedditReady = false;
        mTwitterReady = false;

        RedditFragment redditFragment = RedditFragment.getInstance();
        redditFragment.updateReddit();

        TwitterFragment twitterFragment = TwitterFragment.getInstance();
        twitterFragment.updateTwitter(getContext());
    }

    void setRedditCards(List<CardView> redditCards) {
        mRedditCardList = redditCards;
    }

    void setTwitterCards(List<CardView> twitterCards) {
        mTwitterCardList = twitterCards;
    }

    void setRedditReady(Boolean bool){
        mRedditReady = bool;
    }

    void setTwitterReady(Boolean bool){
        mTwitterReady = bool;
    }

    void createUI() {
        List<CardView> cardViewList = new ArrayList<>();

        if (!mRedditReady || !mTwitterReady) {
            return;
        }

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
