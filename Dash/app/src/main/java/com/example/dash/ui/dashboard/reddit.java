package com.example.dash.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dash.R;

import net.dean.jraw.RedditClient;
import net.dean.jraw.Version;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Account;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.oauth.StatefulAuthHelper;

public class reddit extends Fragment {

    UserAgent userAgent = new UserAgent("bot", "com.example.dash", Version.get(), "aimiroan");
    Credentials credentials = Credentials.script("aimiroan", "", "EVhT39ISlEL1EQ", "cUb6j5hCjkMVx7aa34ZpLsNgN5c");

    NetworkAdapter networkAdapter = new OkHttpNetworkAdapter(userAgent);
    public StatefulAuthHelper helper =
            OAuthHelper.interactive(networkAdapter, credentials);

    public String authUrl = helper.getAuthorizationUrl(true, true, "read");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.reddit_fragment, container, false);

        final WebView browser = rootView.findViewById(R.id.webview);

        browser.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(String url){
                if (helper.isFinalRedirectUrl(url)){
                    browser.stopLoading();

                    RedditClient reddit = helper.onUserChallenge(url);

                    Account me = reddit.me().query().getAccount();
                    System.out.println(me.getName());
                }
            }
        });

        browser.loadUrl(authUrl);

        return rootView;
    }
}
