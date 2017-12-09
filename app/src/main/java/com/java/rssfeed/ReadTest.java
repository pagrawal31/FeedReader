package com.java.rssfeed;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.java.rssfeed.feed.Feed;
import com.java.rssfeed.feed.FeedMessage;
import com.java.rssfeed.filterimpl.ExcludeFeedFilter;
import com.java.rssfeed.filterimpl.FeedFilter;
import com.java.rssfeed.interfaces.IFeedFilter;
import com.java.rssfeed.interfaces.IPageParser;
import com.java.rssfeed.parserimpl.PageScrapBse;
import com.java.rssfeed.parserimpl.RSSFeedParser;

public class ReadTest {
    
    private final static String bseCorporateAnnBaseUrl = "http://www.bseindia.com/corporates/ann.aspx";

	public static Set<FeedMessage> getMessages(int i) {
		Feed feed = null;
		IPageParser parser = null;
		try {
			parser = getFeedParser(i);
			feed = parser.readFeed();
		} catch (Exception e) {
			System.out.println("Failed in getting the Info [" + e.getMessage()
					+ "]");
		}
		if (parser != null) {
			return parser.getMessages();
		}
		return Collections.EMPTY_SET;
	}
	
	public static Set<FeedMessage> getFilteredMessages(int i) {
		Feed feed = null;
		IPageParser parser = null;
		Set<FeedMessage> filteredMsg = new LinkedHashSet<>();
		try {
			parser = getFeedParser(i);
			feed = parser.readFeed();
		} catch (Exception e) {
			System.out.println("Failed in getting the Info [" + e.getMessage()
					+ "]");
		}
		if (parser != null) {
			for (FeedMessage msg : parser.getMessages()) {
				if (parser.filterFeedMessage(msg)) {
					filteredMsg.add(msg);
				}
			}
			return filteredMsg;
		}
		return Collections.EMPTY_SET;
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

    public static void removeAllFilter() {
        int size = FeedInfoStore.getInstance().getFeedSize();
        for (int idx = 0; idx < size; idx++ ) {
            try {
                IPageParser parser = getFeedParser(idx);
                parser.removeFilterAll();
            } catch (Exception e) {

            }
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

}
