package com.patech.feedreader;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.patech.utils.AppConstants;
import com.patech.utils.AppUtils;



/**
 * Created by pagrawal on 08-02-2018.
 */

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void initAds(AdView mAdView) {
        if (mAdView == null)
            return;
        // Ad begins
        MobileAds.initialize(this, AppUtils.ADMOB_ID);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        // Ad ends
    }

//    public String getUid() {
//        return FirebaseAuth.getInstance().getCurrentUser().getUid();
//    }


    public static final String READ_ONLY_USER = "read_only_user";

    public void sendFeedback() {
        String emailId = getResources().getString(R.string.emailId);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", emailId, null));
        String[] addresses = {emailId};
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject: " + getPackageName());
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body:");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses); // String[] addresses
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void rateApp() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void shareApp() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, AppUtils.getSharableText(AppConstants.SHARE_TXT, appPackageName));
        sendIntent.setType("text/html");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }
}
