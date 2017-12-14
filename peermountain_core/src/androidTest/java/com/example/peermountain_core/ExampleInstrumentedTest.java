package com.example.peermountain_core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
//        PeerMountainManager.init(
//                new PeerMountainConfig()
//                        .setApplicationContext(appContext)
//                        .setDebug(true)
//                        .setUserValidTime(1000 * 60 * 5)//5min, after that the user will be asked again to authorize
//                        .setIdCheckLicense("licence-2017-09-12"));
        assertEquals("com.example.peermountain_core.test", appContext.getPackageName());
//        PeerMountainManager.savePin("12345");
//        assertEquals("not same pin","12345",
//                PeerMountainManager.getPin());
    }
}
