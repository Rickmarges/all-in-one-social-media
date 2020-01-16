package com.dash.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dash.Activities.TwitterRepositoryActivity;
import com.dash.R;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TwitterFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static TwitterFragment sInstance;
    private final List<CardView> mCardList = new ArrayList<>();
    private LinearLayout mLinearLayout;

    /**
     * Create the View for Twitter
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
        View rootView = inflater.inflate(R.layout.fragment_twitter, container, false);

        //Set refresh on this page
        mSwipeRefreshLayout = rootView.findViewById(R.id.twitterRefresh);
        // Change colours of bar and background to match style
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(
                R.color.colorBackgroundPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(() -> updateTwitter(getContext()));

        mLinearLayout = rootView.findViewById(R.id.twitterLayout);

        sInstance = this;

        return rootView;
    }

    public void createHomeTimelineView(List<Tweet> tweets) {
        // Make sure the rest of the methods are only called it there are tweets
        mCardList.clear();
        if (tweets == null){
            Toast.makeText(getContext(), "Unable to retrieve tweets", Toast.LENGTH_SHORT).show();
            return;
        }
        // Setup a dynamic linearlayout to add frontpage posts
        if (mLinearLayout == null) {
            mLinearLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.twitterLayout);
        }
        if (mLinearLayout.getChildCount() > 0) {
            mLinearLayout.removeAllViews();
        }
        for (Tweet tweet : tweets) {
            mLinearLayout.addView(createCardView(tweet));
            mCardList.add(createCardView(tweet));
        }
        DashFragment dashFragment = DashFragment.getInstance();
        if (dashFragment == null) {
            dashFragment = new DashFragment();
        }
        dashFragment.setTwitterCards(mCardList);
        dashFragment.setTwitterReady(true);
        dashFragment.createUI();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private CardView createCardView(Tweet tweet) {
        // Initialize the dynamic linearlayout with fields
        LinearLayout cardLayout = new LinearLayout(getContext());
        CardView cardView = new CardView(Objects.requireNonNull(getContext()));

        // Style cardview
        cardView.setCardBackgroundColor(getResources()
                .getColor(R.color.colorBackgroundSecondary, null));
        cardView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        cardView.setUseCompatPadding(true);
        cardView.setCardElevation(7);
        cardView.setRadius(15);
        cardView.setClickable(true);
        cardView.setOnClickListener(view -> {
            String url;
            // Check if the url is in the media or urls List
            if (tweet.id != 0 && tweet.user.screenName != null) {
                url = "https://twitter.com/" + tweet.user.screenName + "/status/" + tweet.id;
            } else {
                Toast.makeText(getContext(), "Unable to open tweet", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);

        });
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        CardView.LayoutParams layoutParams = (CardView.LayoutParams) cardView.getLayoutParams();
        layoutParams.bottomMargin = 10;

        RelativeLayout relativeLayout = new RelativeLayout(getContext());

        relativeLayout.addView(createInfo(tweet));
        relativeLayout.addView(createLogo());

        cardLayout.addView(relativeLayout);
        cardLayout.addView(createDivider());
        cardLayout.addView(createTitle(tweet));
        cardLayout.addView(createDesc(tweet));
        cardLayout.addView(createImage(tweet));
        cardView.addView(cardLayout);

        return cardView;
    }

    private TextView createInfo(Tweet tweet) {
        TextView textViewInfo = new TextView(getContext());

        // Fill and style author
        String author = " By: @" + tweet.user.screenName;
        textViewInfo.append(author);

        // Parse the createdAt format to 'x ago'
        Date date = new Date();
        try {
            date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US)
                    .parse(tweet.createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String time = null;
        if (date != null) {
            time = " " + DateUtils.getRelativeTimeSpanString(date.getTime());
        }


        textViewInfo.append(time);
        textViewInfo.setTextColor(getResources().getColor(R.color.colorTextPrimary, null));
        textViewInfo.setPadding(70, 5, 150, 5);
        textViewInfo.setTypeface(null, Typeface.ITALIC);
        return textViewInfo;
    }

    private View createDivider() {
        View divider = new View(getContext());
        // Style end enable divider
        divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                5));
        divider.setBackgroundResource(R.color.colorTextPrimary);
        return divider;
    }

    private TextView createTitle(Tweet tweet) {
        TextView textViewTitle = new TextView(getContext());
        // Fill and style title
        textViewTitle.setText(tweet.user.name);
        //textViewTitle.setTextAppearance(R.style.strokeColor);
        textViewTitle.setTextColor(getResources().getColor(R.color.colorTextPrimary, null));
        textViewTitle.setGravity(1);
        textViewTitle.setPadding(15, 5, 10, 0);
        textViewTitle.setTextSize(20);
        return textViewTitle;
    }

    private TextView createDesc(Tweet tweet) {
        TextView textViewDesc = new TextView(getContext());
        textViewDesc.setText(tweet.text);
        textViewDesc.setTextColor(getResources().getColor(R.color.colorTextPrimary, null));
        textViewDesc.setPadding(25, 5, 150, 20);
        textViewDesc.setVerticalScrollBarEnabled(true);
        return textViewDesc;
    }

    private ImageView createImage(Tweet tweet) {
        // Insert path into Picasso to download image
        ImageView imageView = new ImageView(getContext());
        if (tweet.entities.media.size() != 0) {
            com.squareup.picasso.Picasso.with(this.getContext())
                    .load(tweet.entities.media.get(0).mediaUrlHttps).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(10, 0, 10, 20);
        }
        return imageView;
    }

    private ImageView createLogo() {
        // Insert path into Picasso to download image
        ImageView imageView = new ImageView(getContext());
            com.squareup.picasso.Picasso.with(this.getContext())
                    .load(com.twitter.sdk.android.R.drawable.tw__ic_logo_blue).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_END);
            imageView.setPadding(20, 20, 10, 20);

        return imageView;
    }

    public static TwitterFragment getInstance() {
        return sInstance;
    }

    void updateTwitter(Context context) {
        TwitterRepositoryActivity.InitializeTwitter(context);
        TwitterRepositoryActivity.GetSingleton().GetHomeTimeline(25);
    }

    public void setRefreshing(boolean bool) {
        mSwipeRefreshLayout.setRefreshing(bool);
    }

    public void clearUI(){
        mCardList.clear();
        if (mLinearLayout.getChildCount() > 0) {
            mLinearLayout.removeAllViews();
        }
    }
}