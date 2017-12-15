package com.peermountain.core.persistence;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by Galeen on 12/15/2017.
 */
@RunWith(AndroidJUnit4.class)
public class SecurePreferencesTest extends PmBaseInstrumentedTest {
    @Test
    public void testPinPrefs() throws Exception {
        SharedPreferenceManager.savePin("12345");
        assertEquals("not same pin","12345",
                SharedPreferenceManager.getPin());
    }
}