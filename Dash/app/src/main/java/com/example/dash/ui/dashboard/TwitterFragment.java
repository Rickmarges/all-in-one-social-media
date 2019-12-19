package com.example.dash.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dash.R;
import com.example.dash.data.TwitterRepository;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

public class TwitterFragment extends Fragment{
    private TwitterLoginButton loginButton;
    private TwitterFragment twitterFragment = this;
    private SwipeRefreshLayout swipeLayout;
    private List<CardView> cardList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_twitter, container, false);
        loginButton = rootView.findViewById(R.id.twitter_login_button);

        if(!isLoggedIn()){
            login();
        }else{

        }

        //Set refresh on this page
        SwipeRefreshLayout swipeLayout = rootView.findViewById(R.id.twitterRefresh);
        // Change colours of bar and background to match style
        swipeLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackgroundPrimary);

        return rootView;
    }

    public boolean isLoggedIn(){
        FileInputStream fis = null;
        try{
            fis = getContext().openFileInput("");
        }catch(Exception ex){

        }
        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
            //Todo if lines contains tokens check
            return true;
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
            return false;
        } finally {
            return false;
        }
    }

    public void login(){
        //Todo get global path name
        File file = new File(getContext().getFilesDir(), "");

        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                if(session != null){
                    Toast.makeText(getContext(), "Authentication succesfull", Toast.LENGTH_SHORT).show();
                    TwitterRepository twitterRepository = new TwitterRepository(session);

                    //Create login session and store it.
                    //Load Twitter data if first time
                    twitterRepository.Login();
                    twitterRepository.GetHomeTimeline(twitterFragment, 20);
                }else{
                    this.failure(new TwitterException("Session could not be created"));
                }
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getContext(), "Authentication failed try again...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    public void createHomeTimelineView(List<Tweet> data) {
        Toast.makeText(getContext(), "Retrieved Twitter data correctly", Toast.LENGTH_SHORT).show();
        return;
    }

    public List<CardView> getCardList(){
        return cardList;
    }
}
