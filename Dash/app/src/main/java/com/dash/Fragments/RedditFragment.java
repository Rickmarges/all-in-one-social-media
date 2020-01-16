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

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dash.Activities.DashboardActivity;
import com.dash.DashApp;
import com.dash.R;
import com.dash.Utils.GenericParser;
import com.securepreferences.SecurePreferences;
import com.squareup.picasso.Picasso;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubmissionPreview;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.pagination.DefaultPaginator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class RedditFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final List<CardView> mCardViewList = new ArrayList<>();
    private LinearLayout mLinearLayout;
    private static RedditFragment sInstance;

    /**
     * Create the View for Reddit
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
        View rootView = inflater.inflate(R.layout.fragment_reddit, container, false);

        mSwipeRefreshLayout = rootView.findViewById(R.id.redditRefresh);
        // Change colours of bar and background to match style
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackgroundPrimary);

        // Set onRefreshListener so it shows progressbar
        mSwipeRefreshLayout.setOnRefreshListener(this::updateReddit);

        mLinearLayout = rootView.findViewById(R.id.redditLayout);

        sInstance = this;

        return rootView;
    }

    /**
     * Updates the Reddit Frontpage when the user returns to this fragment
     */
    @Override
    public void onResume() {
        super.onResume();
        updateReddit();
    }

    /**
     * Executes an async task to refresh Reddit Frontpage
     */
    void updateReddit() {
        if (!DashFragment.getInstance().checkConnection()) {
            return;
        }
        if (DashApp.getAccountHelper().isAuthenticated()) {
            new GetRedditFrontpage().execute();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            DashFragment.getInstance().setRedditReady(false);
            DashFragment.getInstance().setRefreshing(false);
        }
    }

    public static RedditFragment getInstance() {
        return sInstance;
    }

    /**
     * Executes asynctask to retrieve the Reddit Frontpage based on the sorting stored in the Shared Preferences
     */
    class GetRedditFrontpage extends AsyncTask<Void, Void, List<Submission>> {
        final RedditClient redditClient = DashApp.getAccountHelper().getReddit();

        final SubredditSort[] sorts = new SubredditSort[]{
                SubredditSort.HOT,
                SubredditSort.TOP,
                SubredditSort.BEST,
                SubredditSort.CONTROVERSIAL,
                SubredditSort.NEW,
                SubredditSort.RISING
        };

        /**
         * Returns a paginated Reddit Frontpage
         *
         * @param voids voids
         * @return return the next post of the Frontpage
         */
        @Override
        protected List<Submission> doInBackground(Void... voids) {
            try {
                SharedPreferences sharedPreferences = new SecurePreferences(getContext(),
                        "", DashboardActivity.getFilename());
                int savedValue = sharedPreferences.getInt("RedditSort", 0);

                // frontPage() returns a Paginator.Builder
                DefaultPaginator<Submission> frontPage = redditClient.frontPage()
                        .sorting(sorts[savedValue])
                        .limit(25)
                        .build();

                return frontPage.next();
            } catch (NullPointerException | IllegalStateException npe) {
                // Report failure if an Exception occurs
                if (Build.VERSION.SDK_INT >= 26) {
                    Log.w("Reddit warning", "Unable to retrieve frontpage: " + npe.getMessage() + " " + LocalDateTime.now());
                } else {
                    Log.w("Reddit warning", "Unable to retrieve frontpage: " + npe.getMessage());
                }
                return null;
            }
        }

        /**
         * Creates the Cardviews holding the Reddit posts after retrieving them
         *
         * @param submissions The post retrieved from the Reddit Frontpage
         */
        @Override
        protected void onPostExecute(List<Submission> submissions) {
            createUI(submissions);
            DashFragment dashFragment = DashFragment.getInstance();
            if (dashFragment == null) {
                dashFragment = new DashFragment();
            }
            dashFragment.setRedditCards(mCardViewList);
            dashFragment.setRedditReady(true);
            dashFragment.createUI();
        }
    }

    /**
     * Creates the UI elements holding the Cardviews
     *
     * @param submissions The post retrieved from the Reddit Frontpage
     */
    private void createUI(List<Submission> submissions) {
        // Setup a dynamic linearlayout to add frontpage posts
        if (mLinearLayout == null) {
            mLinearLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.redditLayout);
        }
        if (mLinearLayout.getChildCount() > 0) {
            mLinearLayout.removeAllViews();
            mCardViewList.clear();
        }
        // Loop through submissions from frontpage
        if (submissions != null) {
            for (Submission submission : submissions) {
                mLinearLayout.addView(createCardView(submission));
                mCardViewList.add(createCardView(submission));
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Create TextView holding title of the Reddit post
     *
     * @param submission The post retrieved from the Reddit Frontpage
     * @return the TextView holding the title
     */
    private TextView createTitle(Submission submission) {
        TextView textView = new TextView(getContext());
        // Fill TextView with title
        textView.setText(submission.getTitle());
        // Style TextView
        textView.setTextAppearance(R.style.strokeColor);
        textView.setTextColor(getResources().getColor(R.color.colorTextPrimary, null));
        textView.setGravity(1);
        textView.setPadding(15, 5, 10, 0);
        textView.setTextSize(20);
        return textView;
    }

    /**
     * Create TextView holding the author of the Reddit post
     *
     * @param submission The post retrieved from the Reddit Frontpage
     * @return the TextView holding the author
     */
    private TextView createAuthor(Submission submission) {
        TextView textView = new TextView(getContext());
        // Style and fill TextView with r/SubReddit
        String subreddit = "In: r/" + submission.getSubreddit();
        textView.append(subreddit);
        // Append Author after the SubReddit
        String author = " By: u/" + submission.getAuthor();
        textView.append(author);
        // Style TextView
        textView.setTextColor(getResources().getColor(R.color.colorTextPrimary, null));
        textView.setPadding(70, 5, 150, 5);
        textView.setTypeface(null, Typeface.ITALIC);
        return textView;
    }

    /**
     * Create Textview holding the description of the Reddit post
     *
     * @param submission The post retrieved from the Reddit Frontpage
     * @return the TextView holding the description
     */
    private TextView createDesc(Submission submission) {
        TextView textView = new TextView(getContext());
        // Sets the text to hold description of the post
        textView.setText(submission.getSelfText());
        // Style TextView
        textView.setTextColor(getResources().getColor(R.color.colorTextPrimary, null));
        textView.setPadding(25, 5, 150, 5);
        textView.setVerticalScrollBarEnabled(true);
        textView.setHeight(250);
        return textView;
    }

    /**
     * Create TextView holding the text "Read More.."
     *
     * @return the TextView
     */
    private TextView createReadMore() {
        TextView textView = new TextView(getContext());
        // Add text to bottom right of post instead of filling the card with all text
        textView.setText(R.string.read_more);
        // Style TextView
        textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
        textView.setPadding(25, 5, 150, 5);
        textView.setGravity(800005);
        return textView;
    }

    /**
     * Create View that divides author from title and rest of the post
     *
     * @return the view
     */
    private View createDivider() {
        // Style end enable divider
        View view = new View(getContext());
        // Style View
        view.setLayoutParams(new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5));
        view.setBackgroundResource(R.color.colorTextPrimary);
        return view;
    }

    /**
     * Create ImageView holding the image of the Reddit post
     *
     * @param submission The post retrieved from the Reddit Frontpage
     * @return the ImageView
     */
    private ImageView createImage(Submission submission) {
        // Insert path into Picasso to download image
        ImageView imageView = new ImageView(getContext());

        List<SubmissionPreview.Variation> resolutions = submission.getPreview().getImages().get(0).getResolutions();
        Picasso.with(this.getContext()).load(resolutions.get(resolutions.size() - 1).getUrl()).into(imageView);
        // Style ImageView
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);
        imageView.setPadding(10, 0, 10, 20);
        return imageView;
    }

    /**
     * Create Logo holding the image of Reddit
     *
     * @param submission The post retrieved from the Reddit Frontpage
     * @return the ImageView
     */
    private ImageView createLogo() {
        // Insert path into Picasso to download image
        ImageView imageView = new ImageView(getContext());
        //Picasso.with(this.getContext()).load(R.drawable.ic_iconmonstr_reddit_1).into(imageView);
        // Style ImageView
        Drawable background = getResources().getDrawable(R.drawable.ic_iconmonstr_reddit_1);
        imageView.setBackground(background);
        imageView.setVisibility(View.VISIBLE);
        imageView.setScaleX(0.8f);
        imageView.setScaleY(0.8f);
        return imageView;
    }

    /**
     * Create the LinearLayout holding the CardViews
     *
     * @param submission The post retrieved from the Reddit Frontpage
     * @return the LinearLayout
     */
    private LinearLayout createCardLayout(Submission submission) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        // Style LinearLayout
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setPadding(15, 10, 0, 10);
        relativeLayout.addView(createLogo());
        relativeLayout.addView(createAuthor(submission));

        linearLayout.addView(relativeLayout);
        linearLayout.addView(createDivider());
        linearLayout.addView(createTitle(submission));
        // If the post has text instead of image TextViews for the description and Read more
        if (submission.isSelfPost()) {
            linearLayout.addView(createDesc(submission));
            linearLayout.addView(createReadMore());
        }
        // If the post has an image add ImageView
        if (submission.getPreview() != null) {
            linearLayout.addView(createImage(submission));
        }
        return linearLayout;
    }

    /**
     * Create and style the CardViews holding the different elements of the Reddit posts
     *
     * @param submission The post retrieved from the Reddit Frontpage
     * @return the cardview
     */
    private CardView createCardView(Submission submission) {
        CardView cardView = new CardView(Objects.requireNonNull(getContext()));
        // Style CardView
        cardView.setCardBackgroundColor(getResources()
                .getColor(R.color.colorBackgroundSecondary, null));
        cardView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        cardView.setUseCompatPadding(true);
        cardView.setCardElevation(7);
        cardView.setRadius(15);
        CardView.LayoutParams layoutParams = (CardView.LayoutParams) cardView.getLayoutParams();
        layoutParams.bottomMargin = 10;
        // Set onClickListener and add custom animation
        cardView.setForeground(getResources().getDrawable(R.drawable.custom_ripple, null));
        cardView.setClickable(true);
        cardView.setOnClickListener(view -> {
            String url = "https://www.reddit.com" + submission.getPermalink();
            if (GenericParser.isSecureUrl(url)) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        cardView.addView(createCardLayout(submission));
        return cardView;
    }

    /**
     * Clears UI elements
     */
    public void clearUI() {
        mCardViewList.clear();
        if (mLinearLayout.getChildCount() > 0) {
            mLinearLayout.removeAllViews();
        }
    }
}