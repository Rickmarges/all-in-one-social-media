package com.example.dash.data;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.example.dash.BuildConfig;

import java.security.Signature;

//import twitter4j.ResponseList;
//import twitter4j.Status;
//import twitter4j.TwitterFactory;
//import twitter4j.conf.ConfigurationBuilder;
//import twitter4j.Twitter;
import com.twitter.sdk.android.*;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetUi;

// Twitter API Docs: http://twitter4j.org/javadoc/index.html
public class TwitterRepository {
    private Twitter instance;

    public TwitterRepository(String OAuthConsumerToken, String OAuthConsumerTokenSecret, Context context){
        //instance = GetTwitterInstance(OAuthConsumerToken, OAuthConsumerTokenSecret);

        TwitterCore.getInstance();
        TweetComposer.getInstance();
        TweetUi.getInstance();

    }


//    public void GetTwitterTimeline(){
//        try{
//            new GetTwitterTimelineTask().execute(instance);
//        }catch(Exception ex){
//            System.out.println(ex);
//            Log.d("Error", ex.toString());
//        }
//    }

//    private static Twitter GetTwitterInstance(String OAuthAccessToken, String OAuthAccessTokenSecret){
//        ConfigurationBuilder cb = new ConfigurationBuilder();
//        cb.setDebugEnabled(true)
//                .setOAuthConsumerKey(BuildConfig.TWITTER_CONSUMER_ACCESS_TOKEN)
//                .setOAuthConsumerSecret(BuildConfig.TWITTER_CONSUMER_SECRET)
//                .setOAuthAccessToken(OAuthAccessToken)
//                .setOAuthAccessTokenSecret(OAuthAccessTokenSecret);
//        TwitterFactory tf = new TwitterFactory(cb.build());
//        Twitter twitter = tf.getInstance();
//
//        return twitter;
//    }

//    private class GetTwitterTimelineTask extends AsyncTask<Twitter, String, ResponseList<Status>> {
//        protected ResponseList<twitter4j.Status> doInBackground(Twitter... instance) {
//            try{
//                ResponseList<twitter4j.Status> result = instance[0].getHomeTimeline();
//
//                System.out.println(result);
//                // Todo Adapt the data from the API to an generic format.
//
//                return result;
//            }catch(Exception ex){
//                return null;
//            }
//        }
//        protected void onProgressUpdate(String text) {
//            // Todo Might add a loading animation
//        }
//    }
}
