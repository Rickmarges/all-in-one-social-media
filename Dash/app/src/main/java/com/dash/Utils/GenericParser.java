package com.dash.Utils;

import android.text.TextUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class GenericParser {

    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}");
    }

    // When outside URLs are used this method will check the string is a valid URL
    public static boolean isValidUrl(String url) {

        // Check if URL is includes https protocol and fits the regex of a regular url
        if (!url.matches("https://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,4}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)")) {
            return false;
        }

        try {
            // Check if URL is malformed
            URL u = new URL(url);

            // Check if URL can be converted to an URI according to the RFC2396
            u.toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }

        return true;
    }

    // When an URL is opened by the user this will check it's security
    public static boolean isSecureUrl(String url) {
        try {
            URL u = new URL(url);
            // Check if an URLConnection can be opened
            URLConnection urlConnection = u.openConnection();
            // Check if the input stream is correct (if not it isn't using the protocol it says
            urlConnection.getInputStream();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static boolean isValidImage() {
        return false;
    }
}
