package com.java.rssfeed;

import com.java.rssfeed.feed.Feed;
import com.java.rssfeed.filterimpl.FeedFilter;
import com.java.rssfeed.interfaces.IFeedFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FeedInfoStore {

	private static List<Feed> feedInfoList;
    private static Map<String, Integer> feedUrlToIndexMap;
    private static Map<Integer, Feed> indexToFeedMap;
    private int feedCounter = 0;
    private static Set<IFeedFilter> globalFilters;

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
        globalFilters = new HashSet<>();
    }

    public List<IFeedFilter> getGlobalFilters() {
        return new ArrayList<>(globalFilters);
    }

    public void addGlobalFilter(IFeedFilter filter) {
        globalFilters.add(filter);
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

    public void cleanupFilter() {
        globalFilters.clear();
    }
}
