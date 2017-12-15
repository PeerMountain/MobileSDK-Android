package com.peermountain.core.persistence;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.peermountain.core.model.guarded.PeerMountainConfig;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public abstract class PmBaseInstrumentedTest {
    public static Context appContext;

    @BeforeClass
    public static void prepare(){
        if(appContext==null) {
            // Context of the app under test.
            appContext = InstrumentationRegistry.getTargetContext();
            PeerMountainManager.resetProfile();//clean any saved data
            // init the project, this method should be called from app's Application.onCreate
            PeerMountainManager.init(
                    new PeerMountainConfig()
                            .setApplicationContext(appContext)
                            .setDebug(true)
                            .setUserValidTime(1000 * 60 * 5)//5min, after that the user will be asked again to authorize
                            .setIdCheckLicense("licence-2017-09-12"));
        }
    }


//    @AfterClass
//    public static void clear(){
//        // Context of the app under test.
//        appContext = null;
//    }
}
