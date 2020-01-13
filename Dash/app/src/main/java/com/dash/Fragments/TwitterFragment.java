package com.dash.Fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dash.R;
import com.dash.Utils.TwitterRepository;
import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class TwitterFragment extends Fragment {
    private LinearLayout linearLayout;
    private List<CardView> cardList = new ArrayList<>();
    private SwipeRefreshLayout swipeLayout;
    private static TwitterFragment instance;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_twitter, container, false);

        //Set refresh on this page
        swipeLayout = rootView.findViewById(R.id.twitterRefresh);
        // Change colours of bar and background to match style
        swipeLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackgroundPrimary);

        linearLayout = rootView.findViewById(R.id.twitterLayout);

        instance = this;

        TwitterRepository.InitializeTwitter(getActivity().getApplicationContext());
        try {
            TwitterRepository.GetSingleton().GetHomeTimeline(50, this);
        } catch (InterruptedException e) {
            Toast.makeText(getContext(), "Unable to retrieve tweets", Toast.LENGTH_SHORT);
        }

        return rootView;
    }

    public void createHomeTimelineView(List<Tweet> tweets) {
        // Make sure the rest of the methods are only called it there are tweets
        if (tweets == null){
            Toast.makeText(getContext(), "Unable to retrieve tweets", Toast.LENGTH_SHORT);
            return;
        }
        // Setup a dynamic linearlayout to add frontpage posts
        if (linearLayout == null) {
            linearLayout = getActivity().findViewById(R.id.dashLayout);
        }
        if (linearLayout.getChildCount() > 0) {
            linearLayout.removeAllViews();
            cardList.clear();
        }
        for (Tweet tweet : tweets) {
            if (tweet.user.id == TwitterRepository.getCurrentUserId()) continue;
            CardView cardView = createCardView(tweet);
            linearLayout.addView(cardView);
            cardList.add(cardView);
        }
        swipeLayout.setRefreshing(false);
    }

    private TextView createTitle(Tweet tweet) {
        TextView textView = new TextView(getContext());
        textView.setText(tweet.user.name);
        textView.setTextAppearance(R.style.strokeColor);
        textView.setGravity(1);
        textView.setPadding(15, 5, 10, 0);
        textView.setTextSize(20);
        return textView;
    }

    // Fill and style author
    private TextView createAuthor(Tweet tweet) {
        TextView textView = new TextView(getContext());
        String author = " By: @" + tweet.user.screenName;
        textView.append(author);

        // Parse the createdAt format to 'x ago'
        Date date = new Date();
        try {
            date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy").parse(tweet.createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String time = " " + DateUtils.getRelativeTimeSpanString(date.getTime());
        textView.append(time);
        textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
        textView.setPadding(20, 5, 150, 5);
        textView.setTypeface(null, Typeface.ITALIC);
        return textView;
    }

    private TextView createText(Tweet tweet) {
        TextView textView = new TextView(getContext());
        textView.setText(tweet.text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
        textView.setPadding(25, 5, 150, 20);
        textView.setVerticalScrollBarEnabled(true);
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

    private ImageView createImage(Tweet tweet) {
        // Insert path into Picasso to download image
        ImageView imageView = new ImageView(getContext());
        if (tweet.entities.media.size() != 0) {
            com.squareup.picasso.Picasso.with(this.getContext()).load(tweet.entities.media.get(0).mediaUrlHttps).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(10, 0, 10, 20);
        }
        return imageView;
    }

    private LinearLayout createCardLayout(Tweet tweet) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(createAuthor(tweet));
        linearLayout.addView(createDivider());
        linearLayout.addView(createTitle(tweet));
        linearLayout.addView(createText(tweet));
        linearLayout.addView(createImage(tweet));
        return linearLayout;
    }

    private CardView createCardView(Tweet tweet) {
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
        cardView.setOnClickListener(view -> {
            String url;
            // Check if the url is in the media or urls List
            if (tweet.id != 0 && tweet.user.screenName != null) {
                url = "https://twitter.com/" + tweet.user.screenName + "/status/" + tweet.id;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            } else {
                Toast.makeText(getContext(), "Unable to open tweet", Toast.LENGTH_SHORT).show();
            }
        });
        CardView.LayoutParams layoutParams = (CardView.LayoutParams) cardView.getLayoutParams();
        layoutParams.bottomMargin = 10;
        cardView.addView(createCardLayout(tweet));
        return cardView;
    }

    public static TwitterFragment getInstance() {
        return instance;
    }

    void updateTwitter() {

    }
}