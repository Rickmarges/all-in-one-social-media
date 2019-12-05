package com.example.dash.data;

import android.webkit.WebView;

import com.example.dash.R;

import net.dean.jraw.Version;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.oauth.StatefulAuthHelper;

public class RedditAPI {
    UserAgent userAgent = new UserAgent("bot", "com.example.dash", Version.get(), "aimiroan");
    Credentials credentials = Credentials.script("aimiroan", "", "EVhT39ISlEL1EQ", "cUb6j5hCjkMVx7aa34ZpLsNgN5c");

    NetworkAdapter networkAdapter = new OkHttpNetworkAdapter(userAgent);
    final StatefulAuthHelper helper =
            OAuthHelper.interactive(networkAdapter, credentials);

    String authUrl = helper.getAuthorizationUrl(true, true, "read");

    //final WebView browser = new WebView();
}
