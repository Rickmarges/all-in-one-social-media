package com.example.dash.ui.dashboard;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dash.R;
import com.example.dash.data.TwitterRepository;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;

import java.util.ArrayList;
import java.util.List;

public class TwitterFragment extends Fragment {
    private SwipeRefreshLayout swipeLayout;
    private static TwitterFragment instance;
    private List<CardView> cardList = new ArrayList<>();
    private LinearLayout linearLayout;

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

        return rootView;
    }

    public void createHomeTimelineView(List<Tweet> tweets) {
        if (tweets == null) return;
        // Setup a dynamic linearlayout to add frontpage posts
        if (linearLayout == null) {
            linearLayout = getActivity().findViewById(R.id.dashLayout);
        }
        if (linearLayout.getChildCount() > 0) {
            linearLayout.removeAllViews();
        }
        for (Tweet tweet : tweets) {
            linearLayout.addView(createCardView(tweet));
            cardList.add(createCardView(tweet));
        }
        swipeLayout.setRefreshing(false);
    }

    private CardView createCardView(Tweet tweet) {
        // Initialize the dynamic linearlayout with fields
        LinearLayout cardLayout = new LinearLayout(getContext());
        CardView cardView = new CardView(getContext());
        TextView textViewTitle = new TextView(getContext());
        TextView textViewInfo = new TextView(getContext());
        TextView textViewDesc = new TextView(getContext());
        ImageView imageView = new ImageView((getContext()));
        View divider = new View(getContext());

        // Fill and style author
        String author = " By: " + tweet.user.screenName;
        textViewInfo.append(author);
        String time = "Created at: " + tweet.createdAt;
        textViewInfo.append(time);
        textViewInfo.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
        textViewInfo.setPadding(20, 5, 150, 5);
        textViewInfo.setTypeface(null, Typeface.ITALIC);

        // Fill and style title
        textViewTitle.setText(tweet.user.name);
        textViewTitle.setTextAppearance(R.style.strokeColor);
        textViewTitle.setGravity(1);
        //textViewTitle.setTextColor(getResources().getColor(R.color.colorPrimary, null));
        textViewTitle.setPadding(15, 5, 10, 0);
        textViewTitle.setTextSize(20);

        // Insert path into Picasso to download image
        //TODO: Imageloading, adapting to other media if needed
        if (tweet.entities.media != null){
            com.squareup.picasso.Picasso.with(this.getContext()).load(tweet.entities.media.get(0).mediaUrlHttps).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(10, 0, 10, 20);
        }

        // Style end enable divider
        divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5));
        divider.setBackgroundResource(R.color.colorBackgroundPrimary);

        // Style cardview
        cardView.setCardBackgroundColor(getResources().getColor(R.color.colorBackgroundSecondary, null));
        cardView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        cardView.setUseCompatPadding(true);
        cardView.setCardElevation(7);
        cardView.setRadius(15);
        cardView.setClickable(true);
        cardView.setOnClickListener(view -> {
            String url;
            if (tweet.entities.media != null) {
                url = tweet.entities.media.get(0).url;
            } else if (tweet.entities.urls != null) {
                url = tweet.entities.urls.get(0).url;
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

        // Add views to cardlayour and linearlayout
        cardLayout.addView(textViewInfo);
        cardLayout.addView(divider);
        cardLayout.addView(textViewTitle);
        cardLayout.addView(textViewDesc);
        cardLayout.addView(imageView);
        cardView.addView(cardLayout);

        return cardView;
    }

    public static TwitterFragment getInstance() {
        return instance;
    }

    public void update() {

    }
}
