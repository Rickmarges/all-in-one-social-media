package com.example.dash.ui.dashboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dash.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("SetJavaScriptEnabled")
public class RedditFragment extends Fragment {

    private WebView redditWV;
    private SwipeRefreshLayout swipeLayout;
    private String currentUrl = "https://reddit.com/r/pathofexile";
    private List<CardView> cardList = new ArrayList<>();
    private static RedditFragment instance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_reddit, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        instance = this;

        // Enable webview & link it to webview in fragment layout
        redditWV = rootView.findViewById(R.id.wvRddt);
        redditWV.loadUrl(currentUrl);

        // Enable Javascript temporarily till API imports happen
        WebSettings webSettings = redditWV.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        redditWV.setWebViewClient(new MyWebViewClient());

        //Set refresh on this page
        swipeLayout = rootView.findViewById(R.id.redditRefresh);

        swipeLayout.setOnRefreshListener(() -> redditWV.loadUrl(currentUrl));

        // Change colours of bar and background to match style
        swipeLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackgroundPrimary);

        return rootView;
    }

    public static RedditFragment getInstance(){
        return instance;
    }

    public List<CardView> getCardList(){
        return cardList;
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            swipeLayout.setRefreshing(false);
            currentUrl = url;
            super.onPageFinished(view, url);
        }
    }
}
