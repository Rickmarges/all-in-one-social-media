package com.dash.Utils;

import android.graphics.Bitmap;

public class RssItem {
    private String mTitle = "";
    private String mDescription = "";
    private String mLink = "";
    private Bitmap mImage = null;

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getLink() {
        return mLink;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setLink(String link) {
        this.mLink = link;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public void setImage(Bitmap imageUrl) {
        this.mImage = imageUrl;
    }
}