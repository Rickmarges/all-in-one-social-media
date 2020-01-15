package com.dash.Utils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class GenericParser {

    static public boolean isValidUrl(String url) {
        URL u;

        // Check if URL is includes https protocol and fits the regex of a regular url
        if (!url.matches("^(https:)//(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,4}\\b([-a-zA-Z0-9@:%_+.~#?&//=]*)")) {
            return false;
        }

        // Checks if URL is malformed
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }

        // Check if URL can be converted to an URI according to the RFC2396
        try {
            u.toURI();
        } catch (URISyntaxException e) {
            return false;
        }

        return true;
    }

    static public boolean isValidImage() {
        return false;
    }
}
