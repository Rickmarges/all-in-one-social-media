package com.example.dash.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dash.R;

import java.util.ArrayList;
import java.util.List;

public class TwitterFragment extends Fragment {

    private SwipeRefreshLayout swipeLayout;
    private List<CardView> cardList = new ArrayList<>();
    private static TwitterFragment instance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_twitter, container, false);

        //Set refresh on this page
        swipeLayout = rootView.findViewById(R.id.twitterRefresh);
        // Change colours of bar and background to match style
        swipeLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackgroundPrimary);

        instance = this;

        return rootView;
    }

    static TwitterFragment getInstance(){
        return instance;
    }

    void updateTwitter(){

    }
}
