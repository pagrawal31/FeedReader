package com.patech.services;

import com.patech.feedreader.MainActivity;
import com.java.rssfeed.FeedInfoStore;
import com.java.rssfeed.ReadTest;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HelloService extends Service {

	private static final int NOTIFICATION_ID = 1;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressLint("NewApi") @Override
	public void onCreate() {
		super.onCreate();
//		int sleepDuration = 1000;
		int sleepDuration = 300000;
		int size = FeedInfoStore.getInstance().getFeedInfoList().size();
		
		final Intent notificationIntent = new Intent(getApplicationContext(),
				MainActivity.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		final Notification notification = new Notification.Builder(
				getApplicationContext())
				.setSmallIcon(android.R.drawable.arrow_up_float)
				.setOngoing(true).setContentTitle("Music Playing")
				.setContentText("Click to Access Music Player")
				.setContentIntent(pendingIntent).build();

		// Put this Service in a foreground state, so it won't 
		// readily be killed by the system  
		startForeground(NOTIFICATION_ID, notification);
		
		while (true) {
            for (int i = 0; i < size; i++) {
                try {
                	ReadTest.getMessages(i);
                } catch (Exception e) {
                    System.out.println("Failed in getting the Info [" + e.getMessage() + "]");
                }
            }
            try {
                Thread.sleep(sleepDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}

}
