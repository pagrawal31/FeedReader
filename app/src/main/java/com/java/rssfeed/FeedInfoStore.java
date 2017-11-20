package com.java.rssfeed;

import com.java.rssfeed.feed.Feed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedInfoStore {
    private static String[][] feedInfo = {
//        { "http://www.bseindia.com/data/xml/notices.xml", "buyback", "buy back", "buy-back" },
//        { "http://feeds.feedburner.com/nseindia/boardmeet", "buyback", "buy back", "buy-back" },
//        { "http://feeds.feedburner.com/nseindia/ann", "buyback", "buy back", "buy-back" },
//        { "http://feeds.feedburner.com/nseindia/ca", "buyback", "buy back", "buy-back" },
//        { "http://feeds.feedburner.com/nseindia/results", "buyback", "buy back", "buy-back" },
//        { "https://capitalmind.in/tag/buyback/feed/", "buyback", "buy back", "buy-back" },
//        { "http://www.sebi.gov.in/sebirss.xml", "buyback", "buy back", "buy-back" },
//        { "http://www.moneycontrol.com/rss/latestnews.xml", "buyback", "buy back", "buy-back" },
//        { "http://www.bseindia.com/corporates/ann.aspx?expandable=3", "buyback", "buy back", "buy-back"}
        };

	private static List<Feed> feedInfoList;
    private static Map<String, Integer> feedUrlToIndexMap;
    private static Map<Integer, Feed> indexToFeedMap;
    private int feedCounter = 0;

    // create wrapper method for request/release
    private static FeedInfoStore INSTANCE;
    public static FeedInfoStore getInstance() {
        if (INSTANCE == null) {
            synchronized (FeedInfoStore.class) {
                FeedInfoStore localInstance = INSTANCE;
                if (localInstance == null) {
                    localInstance = new FeedInfoStore();
                    INSTANCE = localInstance;
                }
            }
        }
        return INSTANCE;
    }
    private FeedInfoStore() {
        feedUrlToIndexMap = new HashMap<>();
        feedInfoList = new ArrayList<>();
        indexToFeedMap = new HashMap<>();

        for (String[] feedLinkInfo : feedInfo) {
            List<String> subList = new ArrayList<>();
            Feed newFeed = new Feed(null, feedLinkInfo[0], null, null, null, null);
            feedUrlToIndexMap.put(newFeed.getLink(), feedCounter++);
//            for (int filterIdx = 1; filterIdx < feedLinkInfo.length; filterIdx++) {
//                subList.add(feedLinkInfo[filterIdx]);
//            }
            feedInfoList.add(newFeed);
        }
    }

    public List<Feed> getFeedInfoList() {
        return feedInfoList;
    }
    public Feed getFeed(int position) {
        return feedInfoList.get(position);
    }

    public boolean addFeedIntoList(Feed newFeed) {
        feedInfoList.add(newFeed);
        feedUrlToIndexMap.put(newFeed.getLink(), feedCounter++);
        return true;
    }
    public int getFeedSize() {
        return feedInfoList.size();
    }

    // TODO: NavigationMenuFragment should call this after updating database.
    public boolean removeFeedFromList(int position) {
        if (position < feedInfoList.size()) {
            Feed oldFeed = feedInfoList.get(position);
            feedInfoList.remove(position);
            feedUrlToIndexMap.remove(oldFeed.getLink());
            feedCounter--;
            return true;
        }
        return false;
    }

    public Map<String, Integer> getFeedMap() {
        return feedUrlToIndexMap;
    }

    public static void releseHandle(String url) {
		requestInFlight.put(url, 0);
    }
    
    public static boolean requestHandle(String url) {
    	boolean status = false;
    	if (requestInFlight.containsKey(url) && 
				requestInFlight.get(url) == 1) {
			status = false;
		} else {
			requestInFlight.put(url, 1);
			status = true;
		}
    	return status;
    }
    public static Map<String, Integer> requestInFlight = new HashMap<>();
    
}
