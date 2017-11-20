package com.patech.feedreader;

import android.content.Intent;

import com.java.rssfeed.feed.FeedMessage;

public class ActivityUtils {
	static String[] mobileArray = {"none"};
	
	public static void populateIntent(Intent showMessageIntent, FeedMessage currMsg) {
		showMessageIntent.putExtra(MessageViewActivity.AUTHOR,
				currMsg.getAuthor());
		showMessageIntent.putExtra(MessageViewActivity.DESCRIPTION,
				currMsg.getDescription());
		showMessageIntent.putExtra(MessageViewActivity.DATE,
				currMsg.getDate());
		showMessageIntent.putExtra(MessageViewActivity.LINK,
				currMsg.getLink());
		showMessageIntent.putExtra(MessageViewActivity.TITLE,
				currMsg.getTitle());
		showMessageIntent.putExtra(MessageViewActivity.GUID,
				currMsg.getGuid());	
	}
}
