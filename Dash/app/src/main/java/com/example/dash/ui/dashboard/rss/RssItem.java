package com.example.dash.ui.dashboard.rss;

import android.graphics.Bitmap;

public class RssItem {
    private String title = "";
    private String description = "";
    private String link = "";
    private Bitmap image = null;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(Bitmap imageUrl) {
        this.image = imageUrl;
    }
}