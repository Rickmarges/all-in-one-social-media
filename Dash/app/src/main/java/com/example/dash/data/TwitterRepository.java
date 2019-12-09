package com.example.dash.data;

import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.Twitter;

// Twitter API Docs: http://twitter4j.org/javadoc/index.html
class TwitterRepository {
    private Twitter instance;

    public TwitterRepository(String OAuthAccessToken, String OAuthAccessTokenSecret){
        instance = GetTwitterInstance(OAuthAccessToken, OAuthAccessTokenSecret);
    }

    // Search template
    public Object SearchTwitter(){
        instance.search();
        return false;
    }

    // For making functions in other classes.
    private static Twitter GetTwitterInstance(String OAuthAccessToken, String OAuthAccessTokenSecret){
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("*********************")
                .setOAuthConsumerSecret("******************************************")
                .setOAuthAccessToken(OAuthAccessToken)
                .setOAuthAccessTokenSecret(OAuthAccessTokenSecret);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        return twitter;
    }
}
