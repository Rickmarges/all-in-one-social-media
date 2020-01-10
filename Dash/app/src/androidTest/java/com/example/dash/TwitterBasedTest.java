package com.example.dash;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.platform.app.InstrumentationRegistry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.dash.data.TwitterRepository;
import com.twitter.sdk.android.core.GuestSession;
import com.twitter.sdk.android.core.PersistedSessionManager;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken;
import com.twitter.sdk.android.core.internal.persistence.PreferenceStore;
import com.twitter.sdk.android.core.internal.persistence.PreferenceStoreImpl;
import com.twitter.sdk.android.core.internal.persistence.SerializationStrategy;
import com.twitter.sdk.android.core.models.TwitterCollection;

import java.lang.reflect.*;
import java.util.Hashtable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.objenesis.strategy.SerializingInstantiatorStrategy;

import okhttp3.HttpUrl;
import retrofit2.Retrofit;

import static com.twitter.sdk.android.core.TwitterCore.getInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;


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
        Context test_context = InstrumentationRegistry.getTargetContext();
        TwitterRepository.TwitterSingleton.InitializeTwitter(test_context);
        TwitterAuthToken twitterAuthTokenMock = new TwitterAuthToken("Token_Test", "Secret_Test");
        twitterSession = new TwitterSession(twitterAuthTokenMock, 123, "Username_test");
        session = getInstance().getSessionManager();
        session.setSession(123, twitterSession);
        session.setActiveSession(twitterSession);
    }

    //Security tests
    @Test
    public void TwitterKit_SharedpreferenceStorage() {
        PreferenceStore preferenceStore = null;
        try {
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

        SharedPreferences sharedPreferences = preferenceStore.get();
        Boolean b = sharedPreferences.contains("active_twittersession");
        String activeSession = sharedPreferences.getString("active_twittersession", "user_name");
        assertTrue(b);
        assertTrue(activeSession.contains("Token_Test"));
        assertTrue(activeSession.contains("Secret_Test"));
    }

    @Test
    public void TwitterKit_APIusesHTTPS(){
        TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient(twitterSession);
        Retrofit retrofit = (Retrofit) UnitTestExtension.getField(apiClient, "retrofit");
        HttpUrl url = (HttpUrl) UnitTestExtension.getField(retrofit, "baseUrl");
        Assert.assertTrue(url.isHttps());
    }
}


