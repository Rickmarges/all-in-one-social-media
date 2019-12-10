package com.example.dash.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dash.R;
import com.example.dash.ui.dashboard.RSS.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xmlpull.v1.XmlPullParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("SetJavaScriptEnabled")
public class TrendsFragment extends Fragment {

    protected SwipeRefreshLayout swipeLayout;
    protected String currentUrl = "https://trends.google.com/trends/trendingsearches/daily/rss?geo=NL";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.trends_fragment, container, false);

        //Set refresh on this page
        swipeLayout = rootView.findViewById(R.id.trendsRefresh);

        swipeLayout.setOnRefreshListener(() -> updateRss());

        // Change colours of bar and background to match style
        swipeLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackgroundPrimary);

        updateRss();

        return rootView;
    }

    public class RssParser extends AsyncTask<String, Void, List<RssItem>> {
        @Override
        public List<RssItem> doInBackground(String... params) {
            List<RssItem> rssItems = new ArrayList<>();
            Node node;
            NodeList nodes;
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                URL url = new URL(currentUrl);
                Document document = builder.parse(url.openStream());
                document.getDocumentElement().normalize();

                Element root = document.getDocumentElement();

                // channel node
                node = root.getFirstChild().getNextSibling();
                nodes = node.getChildNodes();

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
                                        if (description.contains("&#39;")) {
                                            description = description.replace("&#39;", "'");
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
            } catch (Exception e) {
                rssItems = null;
            }
            return rssItems;
        }

        @Override
        protected void onPostExecute(List<RssItem> rssItems) {
            LinearLayout linearLayout = getActivity().findViewById(R.id.trendsLayout);
            if (linearLayout.getChildCount() > 0) {
                linearLayout.removeAllViews();
            }
            for (RssItem rssItem : rssItems) {
                LinearLayout cardLayout = new LinearLayout(getContext());
                CardView cardView = new CardView(getContext());
                TextView textViewTitle = new TextView(getContext());
                TextView textViewDesc = new TextView(getContext());
                ImageView imageView = new ImageView(getContext());

                textViewTitle.setText(rssItem.getTitle());
                textViewTitle.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                textViewTitle.setPadding(15, 5, 10, 5);
                textViewTitle.setTextSize(20);

                String description = rssItem.getDescription();

                textViewDesc.setText(description.substring(0, 1).toUpperCase() + description.substring(1));
                textViewDesc.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                textViewDesc.setPadding(15, 5, 150, 5);

                imageView.setImageBitmap(rssItem.getImage());

                cardView.setCardBackgroundColor(getResources().getColor(R.color.colorBackgroundPrimary, null));
                cardView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                cardView.setUseCompatPadding(true);
                cardView.setCardElevation(7);
                cardView.setForeground(getResources().getDrawable(R.drawable.custom_ripple, null));
                cardView.setClickable(true);
                cardView.setOnClickListener(view -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(rssItem.getLink()));
                    startActivity(browserIntent);
                });

                CardView.LayoutParams layoutParams = (CardView.LayoutParams)
                        cardView.getLayoutParams();
                layoutParams.height = 220;
                layoutParams.bottomMargin = 10;

                cardLayout.setOrientation(LinearLayout.VERTICAL);

                cardLayout.addView(textViewTitle);
                cardLayout.addView(textViewDesc);
                cardView.addView(cardLayout);
                cardView.addView(imageView);
                linearLayout.addView(cardView);
            }
            swipeLayout.setRefreshing(false);
        }
    }

    private void updateRss() {
        try {
            new RssParser().execute(currentUrl);
        } catch (Exception e) {
            Log.w(getContext().toString(), "Unable to get RSS items");
        }
    }

    private Bitmap getImageBitmap(String imageUrl) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            bitmap = BitmapFactory.decodeStream(bufferedInputStream);
            bufferedInputStream.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e("Error", "Error getting bitmap", e);
        }
        return bitmap;
    }
}
