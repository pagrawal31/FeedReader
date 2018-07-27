package com.patech.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.patech.utils.AppConstants;

public class IncommingCallReceiver extends BroadcastReceiver {

    private SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent){

        try{
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                updateStatus(true);
            }

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                updateStatus(false);
            }
        }
        catch(Exception e){e.printStackTrace();}
    }

    public void updateStatus(boolean onCall) {
        sharedPreferences.edit().putBoolean(AppConstants.ON_CALL, onCall).commit();
    }
}
