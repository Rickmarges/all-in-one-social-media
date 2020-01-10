/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Furthermore this project is licensed under the firebase.google.com/terms and
 * firebase.google.com/terms/crashlytics.
 *
 */

package com.dash.Utils;

import android.graphics.Bitmap;

/**
 * This class creates the standard for an RssItem
 */
public class RssItem {
    private String mTitle = "";
    private String mDescription = "";
    private String mLink = "";
    private Bitmap mImage = null;

    /**
     * Returns the title of the Rss item.
     * @return Title of the Rss item
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the description of the Rss item.
     * @return Description of the Rss item
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Returns the link of the Rss item.
     * @return Link of the Rss item
     */
    public String getLink() {
        return mLink;
    }

    /**
     * Returns the image of the Rss item.
     * @return Image of the Rss item
     */
    public Bitmap getImage() {
        return mImage;
    }

    /**
     * Sets the title of the Rss item.
     * @param title Title of the Rss item
     */
    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * Sets the link of the Rss item.
     * @param link Link of the Rss item.
     */
    public void setLink(String link) {
        this.mLink = link;
    }

    /**
     * Sets the description of the Rss item.
     * @param description Description of the Rss item.
     */
    public void setDescription(String description) {
        this.mDescription = description;
    }

    /**
     * Sets the image url of the Rss item.
     * @param imageUrl Image Url of the Rss item.
     */
    public void setImage(Bitmap imageUrl) {
        this.mImage = imageUrl;
    }
}