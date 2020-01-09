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

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dash.Activities.DashboardActivity;
import com.dash.R;
import com.dash.Utils.RssItem;

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

public class TrendsFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final String mBaseUrl = "https://trends.google.com/trends/trendingsearches/daily/rss?geo=";
    private String mCountryCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_trends, container, false);

        //Set refresh on this page
        mSwipeRefreshLayout = rootView.findViewById(R.id.trendsRefresh);

        mSwipeRefreshLayout.setOnRefreshListener(this::updateRss);

        // Change colours of bar and background to match style
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackgroundPrimary);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(DashboardActivity.getEncryptedEmail(), Context.MODE_PRIVATE);
            mCountryCode = sharedPreferences.getString("Country", "US");
        } catch (NullPointerException npe) {
            Log.w(Objects.requireNonNull(getContext()).toString(), "Couldn't load preferences: " + npe.getMessage());
        }
        updateRss();
    }

    class RssParser extends AsyncTask<String, Void, List<RssItem>> {
        @Override
        public List<RssItem> doInBackground(String... params) {
            List<RssItem> rssItems;
            Node node;
            NodeList nodes;
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                URL url = new URL(mBaseUrl + mCountryCode);
                Document document = builder.parse(url.openStream());
                document.getDocumentElement().normalize();

                Element root = document.getDocumentElement();

                // channel node
                node = root.getFirstChild().getNextSibling();
                nodes = node.getChildNodes();

                rssItems = createRssItems(nodes);
            } catch (ParserConfigurationException | IOException | NullPointerException | SAXException e) {
                rssItems = null;
            }
            return rssItems;
        }

        @Override
        protected void onPostExecute(List<RssItem> rssItems) {
            try {
                createCardUI(rssItems);
            } catch (NullPointerException npe) {
                Log.w(Objects.requireNonNull(getContext()).toString(), "Error creating UI: " + npe.getMessage());
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Unable to update Google Trends", Toast.LENGTH_LONG).show();
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void updateRss() {
        try {
            new RssParser().execute(mBaseUrl + mCountryCode);
        } catch (NullPointerException npe) {
            Log.w(Objects.requireNonNull(getContext()).toString(), "Unable to get RSS items: " + npe.getMessage());
        }
    }

    private Bitmap getImageBitmap(String imageUrl) {
        Bitmap bitmap = null;
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
            Log.w(Objects.requireNonNull(getContext()).toString(), "Error getting bitmap: " + ioe.getMessage());
        }
        return bitmap;
    }

    private List<RssItem> createRssItems(NodeList nodes) {
        List<RssItem> rssItems = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            NodeList itemNodes;
            if (nodes.item(i).getNodeName().equals("item")) {
                RssItem rssItem = new RssItem();
                itemNodes = nodes.item(i).getChildNodes();

                for (int j = 0; j < itemNodes.getLength(); j++) {
                    Node tempNode = itemNodes.item(j);
                    if (tempNode.getNodeName().equals("title")) {
                        rssItem.setTitle(tempNode.getFirstChild().getNodeValue());
                    }
                    if (tempNode.getNodeName().equals("ht:picture")) {
                        rssItem.setImage(getImageBitmap(tempNode.getFirstChild().getNodeValue()));
                    }
                    if (tempNode.getNodeName().equals("ht:news_item")) {
                        Node newsNode = tempNode.getFirstChild();
                        while (newsNode.getNextSibling() != null) {
                            if (newsNode.getNodeName().equals("ht:news_item_title") && rssItem.getDescription().equals("")) {
                                String description = newsNode.getFirstChild().getNodeValue();
                                description = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY).toString();
                                if (description.contains("<") && description.contains(">")) {
                                    description = description.replaceAll("<[^>]*>", "");
                                }
                                rssItem.setDescription(description);
                            }
                            if (newsNode.getNodeName().equals("ht:news_item_url") && rssItem.getLink().equals("")) {
                                rssItem.setLink(newsNode.getFirstChild().getNodeValue());
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
        LinearLayout linearLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.trendsLayout);
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

    private TextView createTitle(String title) {
        TextView textView = new TextView(getContext());
        textView.setText(title);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary, null));
        textView.setPadding(15, 5, 220, 5);
        textView.setTextSize(19);
        return textView;
    }

    private TextView createDesc(String description) {
        TextView textView = new TextView(getContext());
        description = description.substring(0, 1).toUpperCase() + description.substring(1);

        textView.setText(description);
        textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
        textView.setPadding(15, 5, 220, 10);
        textView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.BOTTOM);
        return textView;
    }

    private ImageView createImage(Bitmap bitmap) {
        ImageView imageView = new ImageView(getContext());
        RelativeLayout.LayoutParams imageParams;
        imageParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageView.setImageBitmap(bitmap);
        imageView.setLayoutParams(imageParams);
        imageView.setMinimumHeight(200);
        imageView.setMinimumWidth(200);
        return imageView;
    }

    private CardView createCard(String link) {
        CardView cardView = new CardView(Objects.requireNonNull(getContext()));
        cardView.setCardBackgroundColor(getResources().getColor(R.color.colorBackgroundSecondary, null));
        cardView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        cardView.setUseCompatPadding(true);
        cardView.setCardElevation(7);
        cardView.setRadius(15);
        cardView.setForeground(getResources().getDrawable(R.drawable.custom_ripple, null));
        cardView.setClickable(true);
        cardView.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link))));
        CardView.LayoutParams layoutParams = (CardView.LayoutParams) cardView.getLayoutParams();
        layoutParams.height = 220;
        layoutParams.bottomMargin = 10;
        return cardView;
    }
}
