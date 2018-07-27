package com.java.rssfeed;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.java.rssfeed.model.feed.Feed;
import com.java.rssfeed.model.feed.FeedMessage;
import com.java.rssfeed.interfaces.IFeedFilter;
import com.java.rssfeed.interfaces.IPageParser;
import com.java.rssfeed.parserimpl.PageScrapBse;
import com.java.rssfeed.parserimpl.RSSFeedParser;
import com.patech.enums.FilterLevel;
import com.patech.utils.AppConstants;

public class ReadTest {

    private final static String bseCorporateAnnBaseUrl = AppConstants.PROTOCOL + "://" + AppConstants.BASE_BASE_URL + "ann.aspx";

    public static List<FeedMessage> getMessages(int i, FilterLevel level) {
        if (level == FilterLevel.All) {
            return getFilteredMessages(i);
        } else if (level == FilterLevel.EXCLUDED) {
            return getFilteredMessagesExcluded(i);
        }
        return (List<FeedMessage>) getAllMessages(i);
    }

    public static Collection<FeedMessage> getAllMessages(int i) {
        IPageParser parser = null;
        Feed feed = null;
        try {
            feed = FeedInfoStore.getInstance().getFeed(i);
//            parser = getFeedParser(i);
//            parser.readFeed(feed);
        } catch (Exception e) {
            System.out.println("Failed in getting the Info [" + e.getMessage()
                    + "]");
        }
        if (feed != null) {
            return new ArrayList<>(feed.getMessages());
        }
        return Collections.EMPTY_LIST;
    }

    public static List<FeedMessage> getFilteredMessagesExcluded(int i) {
        IPageParser parser = null;
        List<FeedMessage> filteredMsg = new ArrayList<>();
        try {
            parser = getFeedParser(i);
        } catch (Exception e) {
            System.out.println("Failed in getting the Info [" + e.getMessage()
                    + "]");
        }
        if (parser != null) {
            for (FeedMessage msg : getAllMessages(i)) {
                if (parser.filterFeedMessageExcluded(msg)) {
                    filteredMsg.add(msg);
                }
            }
            return filteredMsg;
        }
        return Collections.EMPTY_LIST;
    }

	public static List<FeedMessage> getFilteredMessages(int i) {
		IPageParser parser = null;
        List<FeedMessage> filteredMsg = new ArrayList<>();
		try {
			parser = getFeedParser(i);
		} catch (Exception e) {
			System.out.println("Failed in getting the Info [" + e.getMessage()
					+ "]");
		}
		if (parser != null) {
			for (FeedMessage msg : getAllMessages(i)) {
				if (parser.filterFeedMessage(msg)) {
					filteredMsg.add(msg);
				}
			}
			return filteredMsg;
		}
		return Collections.EMPTY_LIST;
	}

    public static IPageParser getFeedParser(int idx) throws MalformedURLException {
    	Feed feed = FeedInfoStore.getInstance().getFeedInfoList().get(idx);
		IPageParser parser = getFeedParser(feed);
		return parser;
	}

    public static void addFilterToFeed(Feed newFeed, IFeedFilter filter) {
        IPageParser parser = null;
        try {
            parser = getFeedParser(newFeed);
            parser.addFilter(filter);
        } catch (Exception e) {

        }
    }

    public static void removeAllFilter(int idx) {
        try {
            IPageParser parser = getFeedParser(idx);
            parser.removeFilterAll();
        } catch (Exception e) {

        }
    }

    public static void removeAllFilter() {
        int size = FeedInfoStore.getInstance().getFeedSize();
        for (int idx = 0; idx < size; idx++) {
            removeAllFilter(idx);
        }
    }

    public static void removeFilterFromFeed(IFeedFilter currFilter) {
        int size = FeedInfoStore.getInstance().getFeedSize();
        for (int idx = 0; idx < size; idx++ ) {
            try {
                IPageParser parser = getFeedParser(idx);
                parser.removeFilter(currFilter);
            } catch (Exception e) {

            }
        }
    }

    public static void removeFilterFromFeed(Feed newFeed, IFeedFilter filter) {
        IPageParser parser = null;
        try {
            parser = getFeedParser(newFeed);
            parser.removeFilter(filter);
        } catch (Exception e) {

        }
    }

    public static IPageParser getFeedParser(Feed feed) throws MalformedURLException {
        String urlStr = feed.getLink();
        IPageParser parser = feedParserMap.get(urlStr);
        if (parser == null) {
            if (urlStr.startsWith(bseCorporateAnnBaseUrl)) {
                parser = new PageScrapBse(urlStr);
            } else {
                parser = new RSSFeedParser(urlStr);
            }
            parser.getHeaderFeed();
            feedParserMap.put(urlStr, parser);
        }
        return parser;
    }
    
    private static Map<String, IPageParser> feedParserMap = new HashMap<String, IPageParser>();

    public static void removeAllFeedMsgs() {
        for (int idx = 0; idx < FeedInfoStore.getInstance().getFeedSize(); idx++) {
            removeAllFeedMsgs(idx);
        }
    }

    public static void removeAllFeedMsgs(int idx) {
        IPageParser parser = null;
        Feed feed = FeedInfoStore.getInstance().getFeed(idx);
    
        try {
            parser = getFeedParser(idx);
        } catch (Exception e) {

        }
        if (parser != null) {
            Set<FeedMessage> filteredMsg = new HashSet<>();
            for (FeedMessage msg : getAllMessages(idx)) {
                if (parser.filterFeedMessage(msg)) {
                    msg.setFavorite(true);
                    filteredMsg.add(msg);
                }
            }
            parser.cleanUpFeedMsgs(feed, filteredMsg);
        }
    }
}
