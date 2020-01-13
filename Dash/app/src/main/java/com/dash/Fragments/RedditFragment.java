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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dash.Activities.DashboardActivity;
import com.dash.DashApp;
import com.dash.R;
import com.squareup.picasso.Picasso;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.pagination.DefaultPaginator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RedditFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final List<CardView> mCardViewList = new ArrayList<>();
    private LinearLayout mLinearLayout;
    private static RedditFragment sInstance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_reddit, container, false);

        mSwipeRefreshLayout = rootView.findViewById(R.id.redditRefresh);
        // Change colours of bar and background to match style
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackgroundPrimary);

        mSwipeRefreshLayout.setOnRefreshListener(this::updateReddit);

        mLinearLayout = rootView.findViewById(R.id.redditLayout);

        sInstance = this;

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateReddit();
    }

    void updateReddit() {
        if (DashApp.getAccountHelper().isAuthenticated()) {
            new GetRedditFrontpage().execute();
        }
    }

    static RedditFragment getInstance() {
        return sInstance;
    }

    class GetRedditFrontpage extends AsyncTask<String, Void, List<Submission>> {
        final RedditClient redditClient = DashApp.getAccountHelper().getReddit();

        final SubredditSort[] sorts = new SubredditSort[]{
                SubredditSort.HOT,
                SubredditSort.TOP,
                SubredditSort.BEST,
                SubredditSort.CONTROVERSIAL,
                SubredditSort.NEW,
                SubredditSort.RISING
        };

        @Override
        protected List<Submission> doInBackground(String... params) {
            try {
                SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity())
                        .getSharedPreferences(DashboardActivity
                                .getEncryptedEmail(), Context.MODE_PRIVATE);
                int savedValue = sharedPreferences.getInt("RedditSort", 0);

                // frontPage() returns a Paginator.Builder
                DefaultPaginator<Submission> frontPage = redditClient.frontPage()
                        .sorting(sorts[savedValue])
                        .limit(25)
                        .build();

                return frontPage.next();
            } catch (NullPointerException npe) {
                // Report failure if an Exception occurs
                Log.w(Objects.requireNonNull(getContext()).toString(),
                        "Couldn't load preferences: " + npe.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Submission> submissions) {
            createUI(submissions);
            DashFragment.getInstance().setRedditCards(mCardViewList);
        }
    }

    private void createUI(List<Submission> submissions) {
        // Setup a dynamic linearlayout to add frontpage posts
        if (mLinearLayout == null) {
            mLinearLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.dashLayout);
        }
        if (mLinearLayout.getChildCount() > 0) {
            mLinearLayout.removeAllViews();
            mCardViewList.clear();
        }
        // Loop through submissions from frontpage
        for (Submission submission : submissions) {
            CardView cardView = createCardView(submission);
            mLinearLayout.addView(cardView);
            mCardViewList.add(cardView);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private TextView createTitle(Submission submission) {
        TextView textView = new TextView(getContext());
        textView.setText(submission.getTitle());
        textView.setTextAppearance(R.style.strokeColor);
        textView.setGravity(1);
        textView.setPadding(15, 5, 10, 0);
        textView.setTextSize(20);
        return textView;
    }

    // Fill and style author
    private TextView createAuthor(Submission submission) {
        TextView textView = new TextView(getContext());
        String subreddit = "In: r/" + submission.getSubreddit();
        textView.append(subreddit);
        String author = " By: u/" + submission.getAuthor();
        textView.append(author);
        textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
        textView.setPadding(20, 5, 150, 5);
        textView.setTypeface(null, Typeface.ITALIC);
        return textView;
    }

    private TextView createDesc(Submission submission) {
        TextView textView = new TextView(getContext());
        textView.setText(submission.getSelfText());
        textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
        textView.setPadding(25, 5, 150, 5);
        textView.setVerticalScrollBarEnabled(true);
        textView.setHeight(250);
        return textView;
    }

    private TextView createReadMore() {
        TextView textView = new TextView(getContext());
        textView.setText(R.string.read_more);
        textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
        textView.setPadding(25, 5, 150, 5);
        textView.setGravity(800005);
        return textView;
    }

    private View createDivider() {
        // Style end enable divider
        View view = new View(getContext());
        view.setLayoutParams(new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5));
        view.setBackgroundResource(R.color.colorBackgroundPrimary);
        return view;
    }

    private ImageView createImage(Submission submission) {
        // Insert path into Picasso to download image
        ImageView imageView = new ImageView(getContext());
        Picasso.with(this.getContext()).load(submission.getUrl()).into(imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(10, 0, 10, 20);
        return imageView;
    }

    private LinearLayout createCardLayout(Submission submission) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(createAuthor(submission));
        linearLayout.addView(createDivider());
        linearLayout.addView(createTitle(submission));
        if (submission.isSelfPost()) {
            linearLayout.addView(createDesc(submission));
            linearLayout.addView(createReadMore());
        }
        if (submission.hasThumbnail()) {
            linearLayout.addView(createImage(submission));
        }
        return linearLayout;
    }

    private CardView createCardView(Submission submission) {
        CardView cardView = new CardView(Objects.requireNonNull(getContext()));
        cardView.setCardBackgroundColor(getResources()
                .getColor(R.color.colorBackgroundSecondary, null));
        cardView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        cardView.setUseCompatPadding(true);
        cardView.setCardElevation(7);
        cardView.setRadius(15);
        cardView.setForeground(getResources().getDrawable(R.drawable.custom_ripple, null));
        cardView.setClickable(true);
        cardView.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.reddit.com" + submission.getPermalink()))));
        CardView.LayoutParams layoutParams = (CardView.LayoutParams) cardView.getLayoutParams();
        layoutParams.bottomMargin = 10;
        cardView.addView(createCardLayout(submission));
        return cardView;
    }
}