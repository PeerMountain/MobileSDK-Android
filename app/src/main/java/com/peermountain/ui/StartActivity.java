package com.peermountain.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.peermountain.R;
import com.peermountain.core.odk.XFormActivity;
import com.peermountain.sdk.PeerMountainSDK;


public class StartActivity extends AppCompatActivity {
    private static final int REQUEST_LOGIN = 123;
    private static final int REQUEST_REGISTER = 321;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        PeerMountainSDK.logout();//to test login again

//        showSplash();

        XFormActivity.show(this,
//                "https://www.dropbox.com/s/snukk0uo4z9oz7h/AllFieldsFormTerms%20%282%29.xml?dl=1");
                "https://www.dropbox.com/s/1yctej8ukivs7eo/AllFieldsFormTermsLong.xml?dl=1");
//                "https://www.dropbox.com/s/0n5vs2671j0dgoo/AllFieldsFormTermsLong.xml?dl=1");
//                "https://www.dropbox.com/s/l3yhefnmgxws7wn/AllFieldsFormTerms.xml?dl=1");
//                "https://www.dropbox.com/s/smjkwx2c9kpfs7i/AllFieldsFormNoDateRangeTerms1.xml?dl=1");
//                "https://www.dropbox.com/s/1kouivl58vria46/AllFieldsFormNoDateRangeTerms.xml?dl=1");
//                "https://www.dropbox.com/s/v2yvieuexy8qffa/AllFieldsFormNoDateRange.xml?dl=1");
//                "https://www.dropbox.com/s/u1j62ynrg6cz0sn/AllFieldsForm%20%281%29.xml?dl=1");
//                "https://www.dropbox.com/s/asq0999hwa53vhd/AllFieldsForm.xml?dl=1");
//                "https://www.dropbox.com/s/te701mc861ua9rp/fields.xml_dl%3D1.xml?dl=1");
//                "https://www.dropbox.com/s/1i0eluu3w5vymp4/views_Form.xml?dl=1");
//                "https://www.dropbox.com/s/8zj9t5xylb0gklw/test%20form.xml?dl=1");
//                "https://www.dropbox.com/s/9kj12067gqhst42/Sample%20Form.xml?dl=1");
        finish();
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }

    CountDownTimer timer;

    private void showSplash() {
        timer = new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                PeerMountainSDK.goHome(StartActivity.this);
                timer = null;
                finish();
            }
        };
        timer.start();
    }


}
