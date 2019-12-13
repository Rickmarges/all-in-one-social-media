package com.example.dash.ui.dashboard;

import android.content.Intent;
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

import com.example.dash.R;
import com.example.dash.ui.RedditApp;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.squareup.picasso.Picasso;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.oauth.OAuthException;
import net.dean.jraw.pagination.DefaultPaginator;

import java.util.ArrayList;
import java.util.List;


public class RedditFragment extends Fragment {
    private SwipeRefreshLayout swipeLayout;
    private List<CardView> cardList = new ArrayList<>();
    private static RedditFragment instance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_reddit, container, false);

        instance = this;

        swipeLayout = rootView.findViewById(R.id.redditRefresh);
        // Change colours of bar and background to match style
        swipeLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackgroundPrimary);

        swipeLayout.setOnRefreshListener(() -> updateReddit());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateReddit();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void updateReddit() {
        if (RedditApp.getAccountHelper().isAuthenticated()) {
            new GetRedditFrontpage().execute();
        }
    }

    public static RedditFragment getInstance(){
        return instance;
    }

    public List<CardView> getCardList(){
        return cardList;
    }

    public class GetRedditFrontpage extends AsyncTask<String, Void, Listing<Submission>> {
        RedditClient redditClient = RedditApp.getAccountHelper().getReddit();
        @Override
        protected Listing<Submission> doInBackground(String... params) {
            try {
                // frontPage() returns a Paginator.Builder
                DefaultPaginator<Submission> frontPage = redditClient.frontPage()
                        .sorting(SubredditSort.HOT)
                        .limit(25)
                        .build();

                Listing<Submission> submissions = frontPage.next();
                return submissions;
            } catch (OAuthException e) {
                // Report failure if an OAuthException occurs
                return null;
            }
        }
        @Override
        protected void onPostExecute(Listing<Submission> submissions) {
            createUI(submissions);
        }
    }

    private void createUI(Listing<Submission> submissions){
        // Setup a dynamic linearlayout to add frontpage posts
        LinearLayout linearLayout = getActivity().findViewById(R.id.redditLayout);
        if (linearLayout.getChildCount() > 0) {
            linearLayout.removeAllViews();
        }
        // Loop through submissions from frontpage
        for (Submission s : submissions) {
            boolean hasSelfText = false;

            // Initialize the dynamic linearlayout with fields
            LinearLayout cardLayout = new LinearLayout(getContext());
            CardView cardView = new CardView(getContext());
            TextView textViewTitle = new TextView(getContext());
            TextView textViewInfo = new TextView(getContext());
            TextView textViewDesc = new TextView(getContext());
            TextView textViewReadMore = new TextView(getContext());
            ImageView imageView = new ImageView((getContext()));
            View divider = new View(getContext());

            // Fill and style author
            String subreddit = "In: r/" + s.getSubreddit();
            textViewInfo.append(subreddit);
            String author = " By: u/" + s.getAuthor();
            textViewInfo.append(author);
            textViewInfo.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
            textViewInfo.setPadding(20, 5, 150, 5);
            textViewInfo.setTypeface(null, Typeface.ITALIC);

            // Fill and style title
            textViewTitle.setText(s.getTitle());
            textViewTitle.setTextAppearance(R.style.strokeColor);
            textViewTitle.setGravity(1);
//                textViewTitle.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            textViewTitle.setPadding(15, 5, 10, 0);
            textViewTitle.setTextSize(20);


            // Fill and style description
            if (s.isSelfPost()) {
                textViewDesc.setText(s.getSelfText());
                textViewDesc.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
                textViewDesc.setPadding(25, 5, 150, 5);
                textViewDesc.setVerticalScrollBarEnabled(true);
                textViewDesc.setHeight(250);
                textViewReadMore.setText(R.string.readMore);
                textViewReadMore.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
                textViewReadMore.setPadding(25, 5, 150, 5);
                textViewReadMore.setGravity(800005);
                hasSelfText = true;
            }

            // Insert path into Picasso to download image
            Picasso.get().load(s.getUrl()).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(10, 0, 10, 20);

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
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s.getUrl()));
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
            if (hasSelfText) {
                cardLayout.addView(textViewReadMore);
            }
            cardLayout.addView(imageView);
            cardView.addView(cardLayout);
            linearLayout.addView(cardView);
        }
        swipeLayout.setRefreshing(false);

    }
}
