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
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dash.R;

@SuppressLint("SetJavaScriptEnabled")
class TwitterFragment extends Fragment {

    private WebView twttrWV;
    private SwipeRefreshLayout swipeLayout;
    private String currentUrl = "https://twitter.com/elonmusk";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.twitter_fragment, container, false);

        // Enable webview & link it to webview in fragment layout
        twttrWV = rootView.findViewById(R.id.wvTwttr);
        twttrWV.loadUrl(currentUrl);

        // Enable Javascript temporarily till API imports happen
        WebSettings webSettings = twttrWV.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        twttrWV.setWebViewClient(new MyWebViewClient());

        //Set refresh on this page
        swipeLayout = rootView.findViewById(R.id.twitterRefresh);

        swipeLayout.setOnRefreshListener(() -> twttrWV.loadUrl(currentUrl));

        // Change colours of bar and background to match style
        swipeLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackgroundPrimary);

        return rootView;
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
