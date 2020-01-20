package com.dash;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.dash.Activities.TwitterRepositoryActivity;
import com.securepreferences.SecurePreferences;
import com.twitter.sdk.android.core.SessionManager;
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
import java.util.HashMap;
import java.util.Objects;

import okhttp3.HttpUrl;
import retrofit2.Retrofit;

import static com.twitter.sdk.android.core.TwitterCore.getInstance;
import static org.junit.Assert.assertTrue;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TwitterBasedTest {
    private SessionManager<TwitterSession> session = null;
    private TwitterSession twitterSession = null;
    private Context test_context = null;

    @Before
    public void setUp() {
        //Creates a twitter kit instance
        test_context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        TwitterRepositoryActivity.InitializeTwitter(test_context);
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
        SharedPreferences sharedPreferences = Objects.requireNonNull(preferenceStore).get();
        boolean b = sharedPreferences.contains("active_twittersession");
        String activeSession = sharedPreferences.getString("active_twittersession", "user_name");
        assertTrue(b);
        assertTrue(activeSession.contains("Token_Test"));
        assertTrue(activeSession.contains("Secret_Test"));
    }


    /***
     *  Tests to check if the sensitive twitter data is encrypted in the sharedprefences.
     */
    @Test
    public void SecureSharedPreference(){
        TwitterRepositoryActivity.GetSingleton().savePreferences(twitterSession, test_context,"123");
        SharedPreferences sharedPreferences = new SecurePreferences(InstrumentationRegistry.getInstrumentation().getTargetContext(),"", "123");
        String authTokenDecrypted = sharedPreferences.getString("Twitter token", null);
        String authSecretDecrypted = sharedPreferences.getString("Twitter secret", null);

        SharedPreferences sp = (SharedPreferences) UnitTestExtension.getField(sharedPreferences, "sharedPreferences");
        HashMap<String, String> dataList = (HashMap<String, String>) UnitTestExtension.getField(sp, "mMap");

        //Encrypted versions of token and secret of Token_Test and Secret_Test
        String authTokenIDEncrypted = "d0rdgqYOfXrCYhAHJWhMKMpu2CMdmREvlE025TSc9DU=";
        String authSecretIDEncrypted = "ziVrCeqgdqsLS+3D3rLTtygvoKikCqj6SKSSS6OAHF4=";

        //Getting the secret from the encrypted token and secret key/value
        Object authTokenEncrypted1 = dataList.get(authTokenIDEncrypted);
        Object authSecretEncrypted1 = dataList.get(authSecretIDEncrypted);

        //Tests if the sharedpreference gives the correct data back
        Assert.assertEquals(authTokenDecrypted,"Token_Test");
        Assert.assertEquals(authSecretDecrypted, "Secret_Test");
        //Tests if the data in the sharedpreference is actually encrypted
        Assert.assertNotEquals(authTokenEncrypted1, authTokenDecrypted);
        Assert.assertNotEquals(authSecretEncrypted1, authSecretDecrypted);
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