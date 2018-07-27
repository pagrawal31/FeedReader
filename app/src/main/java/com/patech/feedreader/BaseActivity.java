package com.patech.feedreader;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.patech.utils.AppConstants;
import com.patech.utils.AppUtils;
import com.patech.utils.PermissionUtils;


/**
 * Created by pagrawal on 08-02-2018.
 */

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    public static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final int LOCATION_LAYER_PERMISSION_REQUEST_CODE = 2;
    public static final int WRITE_SETTING_PERMISSION_REQUEST_CODE = 4;
    public static final int READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 8;
    public static final int CALL_PHONE_PERMISSION_REQUEST_CODE = 16;
    public static final int MODIFY_PHONE_STATE_PERMISSION_REQUEST_CODE = 32;
    public static final int READ_CONTACTS_PERMISSION_REQUEST_CODE = 64;


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
        // No Ads for debug build otherwise google can block the developer
        if (BuildConfig.DEBUG)
            return;
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
    public void requestPermission(String permission, String permissionRationale, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            // Display a dialog with rationale.
            PermissionUtils.RationaleDialog
                    .newInstance(requestCode, permission, permissionRationale, false).show(
                    getSupportFragmentManager(), "dialog");
        } else {
            // permission has not been granted yet, request it.
            PermissionUtils.requestPermission(this, requestCode, permission, permissionRationale, false);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == WRITE_SETTING_PERMISSION_REQUEST_CODE &&
//                Settings.System.canWrite(this)){
//            Toast.makeText(getApplicationContext(),
//                    android.Manifest.permission.WRITE_SETTINGS + "Permission Granted ",
//                    Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getApplicationContext(),
//                    android.Manifest.permission.WRITE_SETTINGS + "Permission NOT Granted ",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Manifest.permission.WRITE_SETTINGS
        if (requestCode == MY_LOCATION_PERMISSION_REQUEST_CODE) {
            // Enable the My Location button if the permission has been granted.
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
//                mUiSettings.setMyLocationButtonEnabled(true);
//                mMyLocationButtonCheckbox.setChecked(true);
            } else {
//                mLocationPermissionDenied = true;
            }

        } else if (requestCode == LOCATION_LAYER_PERMISSION_REQUEST_CODE) {
            // Enable the My Location layer if the permission has been granted.
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
//                mMap.setMyLocationEnabled(true);
//                mMyLocationLayerCheckbox.setChecked(true);
            } else {
//                mLocationPermissionDenied = true;
            }
        } else if (requestCode == WRITE_SETTING_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    android.Manifest.permission.WRITE_SETTINGS)) {
                Toast.makeText(getApplicationContext(),
                        android.Manifest.permission.WRITE_SETTINGS + "Permission Granted",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        android.Manifest.permission.WRITE_SETTINGS + "Permission NOT Granted",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MODIFY_PHONE_STATE_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    android.Manifest.permission.MODIFY_PHONE_STATE)) {
                Toast.makeText(getApplicationContext(),
                        android.Manifest.permission.MODIFY_PHONE_STATE + "Permission Granted",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        android.Manifest.permission.MODIFY_PHONE_STATE + "Permission Not Granted",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == READ_PHONE_STATE_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    android.Manifest.permission.READ_PHONE_STATE)) {
//                Toast.makeText(getApplicationContext(),
//                        android.Manifest.permission.READ_PHONE_STATE + " Permission Granted ",
//                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        android.Manifest.permission.READ_PHONE_STATE + " Permission Not Granted ",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
