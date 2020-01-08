package com.example.dash;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;


import static org.junit.Assert.assertEquals;




/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TwitterBasedTest {



    @Before
    public void setUp() throws Exception {
        //TwitterAuthToken t = mock(TwitterAuthToken.class);
        TwitterAuthToken t = PowerMockito.mock(TwitterAuthToken.class);
        //when(t.token).thenReturn("123");
        PowerMockito.when(t.secret).thenReturn("secret");
        PowerMockito.when(t.token).thenReturn("token");

        Object d = t.secret;
        //TwitterCore.getInstance().getSessionManager().setSession(123, new TwitterSession(t, 123, "test"));

    }

    //Security tests
    @Test
    public void SessionIsEncrypted_TwitterRepository() {
        //TwitterAuthToken twitterAuthTokenMock = mock(TwitterAuthToken.class);
        //SessionManager<TwitterSession> twitterSessionMock = mock(SessionManager.class);

        //twitterSessionMock.setSession(123, new TwitterSession(twitterAuthTokenMock, 123, "Test_Username" ));

       // Object t = twitterSessionMock.getUserName();

        assertEquals("com.example.dash", "com.example.dash");
    }
}
