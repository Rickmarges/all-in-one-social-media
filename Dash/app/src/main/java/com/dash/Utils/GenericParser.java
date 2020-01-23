package com.dash.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class GenericParser {

    /**
     * Check if a valid email is entered
     * SuppressWarning because it makes more sense to return true if it's a valid email
     * and do something when it's not a valid email
     *
     * @param email the email to check
     * @return if the email is valid or not
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Checks if a valid password is entered during registration
     *
     * @param password the password to check
     * @return if the password is valid or not
     */
    public static boolean isValidPassword(String password) {
        return password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\s).{8,100}");
    }

    /**
     * Checks if the String is a valid URL
     *
     * @param url the String to check
     * @return if it's a valid URL or not
     */
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

    /**
     * Checks the security of an URL
     *
     * @param url the URL to check
     * @return if it's secure or not
     */
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

    /**
     * Checks if an URL from an image is valid
     * @param url the URL to check
     * @param source the Fragment the URL comes from
     * @return if the URL is valid or not
     */
    public static boolean isValidImageUrl(String url, String source) {
        if (!isValidUrl(url)) {
            return false;
        }
        switch (source) {
            case "reddit":
                return url.matches("https://(external-)?preview\\.redd\\.it/.*\\.(jpg|png)\\?.*");
            case "twitter":
                return url.matches("https://pbs\\.twimg\\.com/(media|ext_tw_video_thumb)/.*[.=](jpg|png).*");
            case "trends":
                return url.matches("https://t[0-9].gstatic.com/images\\?q=tbn:[a-zA-Z0-9-_]{80,85}");
            default:
                return false;
        }
    }
}
