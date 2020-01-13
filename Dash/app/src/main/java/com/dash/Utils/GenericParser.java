package com.example.dash.ui;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class GenericParser {

    static public boolean isValidUrl(String url){
        URL u;

        // Check if URL is includes https protocol
        if (!url.matches("^https://")){
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

    static public boolean isValidImage(){
        return false;
    }
}
