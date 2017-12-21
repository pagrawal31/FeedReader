package com.patech.services;

import java.util.HashSet;
import java.util.Set;

import com.patech.feedreader.ActivityUtils;
import com.patech.feedreader.FeedReaderApplication;
import com.patech.feedreader.MainActivity;
import com.patech.feedreader.MessageViewActivity;
import com.patech.feedreader.R;
import com.java.rssfeed.feed.Feed;
import com.java.rssfeed.FeedInfoStore;
import com.java.rssfeed.feed.FeedMessage;
import com.java.rssfeed.ReadTest;
import com.java.rssfeed.interfaces.IPageParser;
import com.patech.location.Connectivity;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

public class FeedIntentService extends IntentService {

	private Set<FeedMessage> notifiedSet;
	public FeedIntentService() {
		super(FeedIntentService.class.getName());
		notifiedSet = new HashSet<>();
	}

	private static final int NOTIFICATION_ID = 1;
	private static int NOTIFICATION_COUNTER = 2;
    private final Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private long[] mVibratePattern = { 0, 200, 200, 300 };

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

//	@Override
	@SuppressLint("NewApi")
	public void onCreate2() {
//		super.onCreate();

		final Intent notificationIntent = new Intent(getApplicationContext(),
				MainActivity.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		final Notification notification = new Notification.Builder(
				getApplicationContext())
				.setSmallIcon(R.drawable.ic_launcher)
				.setOngoing(true).setContentTitle("Service Running")
				.setContentText("Go to Feed Reader app")
				.setContentIntent(pendingIntent).build();

		// Put this Service in a foreground state, so it won't
		// readily be killed by the system
		startForeground(NOTIFICATION_ID, notification);

		int size = FeedInfoStore.getInstance().getFeedInfoList().size();

		while (true) {
		    // if user is on Data but does not want update on WiFi only, then we should not update on Data
            boolean isUpdateWifiOnly = ((FeedReaderApplication)getApplication()).isUpdateOnWifiOnly();
            boolean isUpdate = false;
            if (isUpdateWifiOnly) {
                isUpdate = Connectivity.isConnectedWifi(getApplicationContext());
            } else {
                isUpdate = true;
            }
            if (isUpdate && Connectivity.isConnected(getApplicationContext())) {
                for (int i = 0; i < size; i++) {
                    try {
                        IPageParser parser = null;
                        Feed feed = FeedInfoStore.getInstance().getFeed(i);
                        try {
                            parser = ReadTest.getFeedParser(feed);
                            parser.readFeed(feed);
                            for (FeedMessage msg : parser.getMessages()) {
                                if (parser.filterFeedMessage(msg)) {
                                    showNotification(feed.getLink(), msg);
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Failed in getting the Info [" + e.getMessage()
                                    + "]");
                        }
                    } catch (Exception e) {
                        System.out.println("Failed in getting the Info [" + e.getMessage() + "]");
                    }
                }
            }
            try {
				long duration = ((FeedReaderApplication)getApplication()).getFrequencyInMillis();
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}

	@SuppressLint("NewApi")
	public void showNotification(String infoUrl, FeedMessage feedMsg) {

		if (notifiedSet.contains(feedMsg)) {
			return;
		}

		notifiedSet.add(feedMsg);

        Intent showMessageIntent = new Intent(this, MessageViewActivity.class);
        ActivityUtils.populateIntent(showMessageIntent, feedMsg);

        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), showMessageIntent, 0);

		String msg = feedMsg.getTitle() + " " + infoUrl + " " + feedMsg.getDescription();

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification.Builder builder   = new Notification.Builder(this)
                .setContentTitle(feedMsg.getTitle())
                .setContentText(msg)
                .setTicker(msg)
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
				.setSound(alarmSound)
                .setVibrate(mVibratePattern);

//        builder.setDefaults(Notification.DEFAULT_SOUND);
//        builder.setOnlyAlertOnce(true);


//                .addAction(android.R.drawable.alert_light_frame, "Call", pIntent)
//                .addAction(android.R.drawable.sym_action_chat, "More", pIntent)
//                .addAction(android.R.drawable.sym_action_email, "And more", pIntent);

//		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
//		stackBuilder.addParentStack(MainActivity.class);
//		stackBuilder.addNextIntent(showMessageIntent);

//		PendingIntent resultPendingIntent =
//		        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.

        mNotificationManager.notify(NOTIFICATION_COUNTER++, builder.build());

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		onCreate2();
	}

}
