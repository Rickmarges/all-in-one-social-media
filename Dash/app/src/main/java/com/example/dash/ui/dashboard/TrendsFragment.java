package com.example.dash.ui.dashboard;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dash.R;
import com.example.dash.ui.dashboard.rss.RssItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

public class TrendsFragment extends Fragment {
    private SwipeRefreshLayout swipeLayout;
    private final String baseUrl = "https://trends.google.com/trends/trendingsearches/daily/rss?geo=";
    private String countryCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_trends, container, false);

        //Set refresh on this page
        swipeLayout = rootView.findViewById(R.id.trendsRefresh);

        swipeLayout.setOnRefreshListener(this::updateRss);

        // Change colours of bar and background to match style
        swipeLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackgroundPrimary);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(((DashboardActivity) getActivity()).getEncryptedEmail(), Context.MODE_PRIVATE);
            countryCode = sharedPreferences.getString("Country", "US");
        } catch (Exception e) {
            Log.d("Warning", "Couldn't load preferences!");
        }
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

                URL url = new URL(baseUrl + countryCode);
                Document document = builder.parse(url.openStream());
                document.getDocumentElement().normalize();

                Element root = document.getDocumentElement();

                // channel node
                node = root.getFirstChild().getNextSibling();
                nodes = node.getChildNodes();

                rssItems = createRssItems(nodes);
            } catch (Exception e) {
                rssItems = null;
            }
            return rssItems;
        }

        @Override
        protected void onPostExecute(List<RssItem> rssItems) {
            try {
                createCardUI(rssItems);
            } catch (Exception e) {
                Log.d("TEST", Objects.requireNonNull(e.getMessage()));
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Unable to update Google Trends", Toast.LENGTH_LONG).show();
            }
            swipeLayout.setRefreshing(false);
        }
    }

    private void updateRss() {
        try {
            new RssParser().execute(baseUrl + countryCode);
        } catch (Exception e) {
            Log.w(Objects.requireNonNull(getContext()).toString(), "Unable to get RSS items");
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
            CardView cardView = new CardView(Objects.requireNonNull(getContext()));
            TextView textViewTitle = new TextView(getContext());
            TextView textViewDesc = new TextView(getContext());
            ImageView imageView = new ImageView(getContext());

            textViewTitle.setText(rssItem.getTitle());
            textViewTitle.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            textViewTitle.setPadding(15, 5, 220, 5);
            textViewTitle.setTextSize(19);

            String description = rssItem.getDescription();
            description = description.substring(0, 1).toUpperCase() + description.substring(1);
            ;

            textViewDesc.setText(description);
            textViewDesc.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
            textViewDesc.setPadding(15, 5, 220, 10);
            textViewDesc.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            textViewDesc.setGravity(Gravity.BOTTOM);

            RelativeLayout.LayoutParams imageParams;
            imageParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            imageView.setImageBitmap(rssItem.getImage());
            imageView.setLayoutParams(imageParams);
            imageView.setMinimumHeight(200);
            imageView.setMinimumWidth(200);

            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorBackgroundSecondary, null));
            cardView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            cardView.setUseCompatPadding(true);
            cardView.setCardElevation(7);
            cardView.setRadius(15);
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

            cardLayout.addView(textViewTitle);
            cardLayout.addView(textViewDesc);
            cardLayout.addView(imageView);
            cardView.addView(cardLayout);
            linearLayout.addView(cardView);
        }
    }
}
