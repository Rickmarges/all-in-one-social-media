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
 * Creates the standard for an RssItem
 */
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