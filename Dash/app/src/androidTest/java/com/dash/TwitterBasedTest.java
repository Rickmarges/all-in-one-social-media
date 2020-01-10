package com.dash;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dash.Utils.TwitterRepository;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.persistence.PreferenceStore;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import okhttp3.HttpUrl;
import retrofit2.Retrofit;

import static com.twitter.sdk.android.core.TwitterCore.getInstance;
import static org.junit.Assert.assertTrue;

//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.platform.app.InstrumentationRegistry;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TwitterBasedTest {
    SessionManager<TwitterSession> session = null;
    TwitterSession twitterSession = null;

    @Before
    public void setUp() throws Exception {
        //Creates a twitter kit instance
        Context test_context = InstrumentationRegistry.getTargetContext();
        TwitterRepository.TwitterSingleton.InitializeTwitter(test_context);
        TwitterAuthToken twitterAuthToken = new TwitterAuthToken("Token_Test", "Secret_Test");
        //Creating a session to use for the tests
        twitterSession = new TwitterSession(twitterAuthToken, 123, "Username_test");
        session = getInstance().getSessionManager();
        session.setSession(123, twitterSession);
        session.setActiveSession(twitterSession);
    }

    @After
    public void setDown(){
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
    }

    //Security tests
    //Tests if the twitterkit correctly inserts data in sharedpreference
    @Test
    public void TwitterKit_SharedpreferenceStorage() {
        PreferenceStore preferenceStore = null;
        try {
            //using reflection to get to the private field
            Field field = session.getClass().getDeclaredField("preferenceStore");
            field.setAccessible(true);
            try {
                preferenceStore = (PreferenceStore) field.get(session);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        //gets the sharedprefence
        SharedPreferences sharedPreferences = preferenceStore.get();
        Boolean b = sharedPreferences.contains("active_twittersession");
        String activeSession = sharedPreferences.getString("active_twittersession", "user_name");
        assertTrue(b);
        assertTrue(activeSession.contains("Token_Test"));
        assertTrue(activeSession.contains("Secret_Test"));
    }

    //Tests if the connection to the api is https
    @Test
    public void TwitterKit_APIusesHTTPS(){
        //creating an api instance to be used.
        TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient(twitterSession);
        //Using reflection to get to the private fields of the api instance and check if its https
        Retrofit retrofit = (Retrofit) UnitTestExtension.getField(apiClient, "retrofit");
        HttpUrl url = (HttpUrl) UnitTestExtension.getField(retrofit, "baseUrl");
        Assert.assertTrue(url.isHttps());
    }
}