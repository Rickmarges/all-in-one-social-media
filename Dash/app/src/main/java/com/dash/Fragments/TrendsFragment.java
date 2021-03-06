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

package com.dash.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dash.Activities.DashboardActivity;
import com.dash.R;
import com.dash.Utils.GenericParser;
import com.dash.Utils.RssItem;
import com.securepreferences.SecurePreferences;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class TrendsFragment extends Fragment {
    private String mCountryCode;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final String mBaseUrl = "https://trends.google.com/trends/trendingsearches/daily/rss?geo=";

    /**
     * Create the View for Trends
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     *                           The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_trends, container, false);

        //Set refresh on this page
        mSwipeRefreshLayout = rootView.findViewById(R.id.trendsRefresh);

        // Set onRefreshListener on this page top update the Trends
        mSwipeRefreshLayout.setOnRefreshListener(this::updateRss);

        // Change colours of bar and background to match style
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackgroundPrimary);

        return rootView;
    }

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        try {
            SharedPreferences sharedPreferences = new SecurePreferences(getActivity().getApplicationContext(),
                    "", DashboardActivity.getFilename());
            mCountryCode = sharedPreferences.getString("Country", "NL");
        } catch (NullPointerException npe) {
            Log.w(Objects.requireNonNull(getContext()).toString(),
                    "Couldn't load preferences: " + npe.getMessage());
        }
        updateRss();
    }

    /**
     *
     */
    class RssParser extends AsyncTask<String, Void, List<RssItem>> {
        /**
         * Builds document holding RssItems
         *
         * @param params params
         * @return RssItems
         */
        @Override
        public List<RssItem> doInBackground(String... params) {
            String urlString = mBaseUrl + mCountryCode;
            List<RssItem> rssItems = new ArrayList<>();
            if (GenericParser.isSecureUrl(urlString) != null) {
                Node node;
                NodeList nodes;
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();

                    URL url = new URL(urlString);
                    Document document = builder.parse(url.openStream());
                    document.getDocumentElement().normalize();

                    Element root = document.getDocumentElement();

                    // channel node
                    node = root.getFirstChild().getNextSibling();
                    nodes = node.getChildNodes();

                    rssItems = createRssItems(nodes);
                } catch (ParserConfigurationException | IOException | SAXException e) {
                    rssItems = null;
                    Log.w(Objects.requireNonNull(getContext()).toString(),
                            "Unable to parse RSS: " + e.getMessage());
                }
            }
            return rssItems;
        }

        /**
         * Populate CardUI with RssItems
         *
         * @param rssItems retrieved from RssParser
         */
        @Override
        protected void onPostExecute(List<RssItem> rssItems) {
            try {
                if (rssItems.size() > 0) {
                    createCardUI(rssItems);
                }
            } catch (NullPointerException npe) {
                Log.w(Objects.requireNonNull(getContext()).toString(),
                        "Error creating UI: " + npe.getMessage());
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        "Unable to update Google Trends", Toast.LENGTH_LONG).show();
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Update the RSS by executing the RssParser
     */
    private void updateRss() {
        if (!DashFragment.getInstance().checkConnection()) {
            return;
        }
        try {
            new RssParser().execute(mBaseUrl + mCountryCode);
        } catch (NullPointerException npe) {
            Log.w(Objects.requireNonNull(getContext()).toString(),
                    "Unable to get RSS items: " + npe.getMessage());
        }
    }

    /**
     * Check url to make sure the picture is from the right website
     * Retrieve bitmap from an URL
     *
     * @param imageUrl url from RssItem from where to download
     * @return the download bitmap
     */
    private Bitmap getImageBitmap(String imageUrl) {
        Bitmap bitmap;
        if (!GenericParser.isValidImageUrl(imageUrl, "trends")) {
            return setDefault();
        }
        try {
            URL url = new URL(imageUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            bitmap = BitmapFactory.decodeStream(bufferedInputStream);
            bufferedInputStream.close();
            inputStream.close();
        } catch (IOException ioe) {
            Log.w(Objects.requireNonNull(getContext()).toString(),
                    "Error getting bitmap: " + ioe.getMessage());
            return setDefault();
        }
        return bitmap;
    }

    private Bitmap setDefault() {
        Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_photo_found);
        return Bitmap.createScaledBitmap(defaultBitmap, 200, 200, false);
    }

    /**
     * Creates a List with RssItems
     * loops through list to get data from nodes retrieved from excecuting RssParser
     *
     * @param nodes nodes retrieved from Asynctask RssParser
     * @return an RssItem
     */
    private List<RssItem> createRssItems(NodeList nodes) {
        List<RssItem> rssItems = new ArrayList<>();
        // Loop through nodes
        for (int i = 0; i < nodes.getLength(); i++) {
            NodeList itemNodes;
            if (nodes.item(i).getNodeName().equals("item")) {
                RssItem rssItem = new RssItem();
                itemNodes = nodes.item(i).getChildNodes();
                // Retrieve Title and set it to the Title in RssItem
                for (int j = 0; j < itemNodes.getLength(); j++) {
                    Node tempNode = itemNodes.item(j);
                    if (tempNode.getNodeName().equals("title")) {
                        rssItem.setTitle(tempNode.getFirstChild().getNodeValue());
                    }
                    // Retrieve ImageBitmap and set it to the Image in RssItem
                    if (tempNode.getNodeName().equals("ht:picture")) {
                        rssItem.setImage(getImageBitmap(tempNode.getFirstChild().getNodeValue()));
                    }
                    // Retrieve NewsItem
                    if (tempNode.getNodeName().equals("ht:news_item")) {
                        Node newsNode = tempNode.getFirstChild();
                        // Retrieve the description and put it in Description in RssItem, after stripping HTML character from it
                        while (newsNode.getNextSibling() != null) {
                            if (newsNode.getNodeName().equals("ht:news_item_title")
                                    && rssItem.getDescription().equals("")) {
                                String description = newsNode.getFirstChild().getNodeValue();
                                description = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)
                                        .toString();
                                if (description.contains("<") && description.contains(">")) {
                                    description = description
                                            .replaceAll("<[^>]*>", "");
                                }
                                rssItem.setDescription(description);
                            }
                            // Retrieve the URL and set it to Link in RssItem
                            if (newsNode.getNodeName().equals("ht:news_item_url")
                                    && rssItem.getLink().equals("")) {
                                String url = newsNode.getFirstChild().getNodeValue();
                                if (GenericParser.isSecureUrl(url) != null) {
                                    rssItem.setLink(url);
                                }
                            }
                            newsNode = newsNode.getNextSibling();
                        }
                    }
                }
                rssItems.add(rssItem);
            }
        }
        return rssItems;
    }

    /**
     * Creates the UI based on CardView from a list of RssItems/nodes.
     *
     * @param rssItems The list of RssItems which will be parsed into CardViews.
     */
    private void createCardUI(List<RssItem> rssItems) {
        LinearLayout linearLayout = Objects.requireNonNull(getActivity())
                .findViewById(R.id.trendsLayout);
        if (linearLayout.getChildCount() > 0) {
            linearLayout.removeAllViews();
        }
        for (RssItem rssItem : rssItems) {
            RelativeLayout cardLayout = new RelativeLayout(getContext());
            CardView cardView = createCard(rssItem.getLink());
            cardLayout.addView(createTitle(rssItem.getTitle()));
            cardLayout.addView(createDesc(rssItem.getDescription()));
            cardLayout.addView(createImage(rssItem.getImage()));
            cardView.addView(cardLayout);
            linearLayout.addView(cardView);
        }
    }

    /**
     * Create TextView holding title of the RssItem
     *
     * @param title title retrieved from the RssItem
     * @return the TextView holding the title
     */
    private TextView createTitle(String title) {
        TextView textView = new TextView(getContext());
        // Fill Textview with title
        textView.setText(title);
        // Style TextView
        textView.setTextColor(getResources().getColor(R.color.colorTextPrimary, null));
        textView.setPadding(15, 5, 220, 5);
        textView.setTextSize(19);
        return textView;
    }

    /**
     * Create TextView holding description of the RssItem
     *
     * @param description title retrieved from the RssItem
     * @return the TextView holding the description
     */
    private TextView createDesc(String description) {
        TextView textView = new TextView(getContext());
        // Fill Textview with escription and make first character uppercase
        description = description.substring(0, 1).toUpperCase() + description.substring(1);
        textView.setText(description);
        // Style TextView
        textView.setTextColor(getResources().getColor(R.color.colorTextPrimary, null));
        textView.setPadding(15, 5, 220, 10);
        textView.setLayoutParams(new RelativeLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.BOTTOM);
        return textView;
    }

    /**
     * Create ImageView holding bitmap of the RssItem
     *
     * @param bitmap bitmap retrieved from the RssItem
     * @return the ImageView holding the bitmap
     */
    private ImageView createImage(Bitmap bitmap) {
        ImageView imageView = new ImageView(getContext());
        // Fill ImageView with bitmap
        imageView.setImageBitmap(bitmap);
        // Style ImageView
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageView.setLayoutParams(imageParams);
        imageView.setMinimumHeight(200);
        imageView.setMinimumWidth(200);
        return imageView;
    }

    /**
     * Create TextView holding url of the RssItem
     *
     * @param link url retrieved from the RssItem
     * @return the TextView holding the url
     */
    private CardView createCard(String link) {
        CardView cardView = new CardView(Objects.requireNonNull(getContext()));
        // Style CardView
        cardView.setCardBackgroundColor(getResources()
                .getColor(R.color.colorBackgroundSecondary, null));
        cardView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        cardView.setUseCompatPadding(true);
        cardView.setCardElevation(7);
        cardView.setRadius(15);
        CardView.LayoutParams layoutParams = (CardView.LayoutParams) cardView.getLayoutParams();
        layoutParams.height = 220;
        layoutParams.bottomMargin = 10;
        // Set onClickListener and add custom animation
        cardView.setForeground(getResources().getDrawable(R.drawable.custom_ripple, null));
        cardView.setClickable(true);
        cardView.setOnClickListener(view -> {

            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(link)));
        });

        return cardView;
    }
}
