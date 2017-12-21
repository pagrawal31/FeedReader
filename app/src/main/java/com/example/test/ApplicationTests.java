package com.example.test;

import static org.junit.Assert.*;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import com.java.rssfeed.FeedInfoStore;
import com.java.rssfeed.ReadTest;
import com.java.rssfeed.feed.Feed;
import com.java.rssfeed.feed.FeedMessage;
import com.java.rssfeed.parserimpl.PageScrapBse;
import com.java.rssfeed.parserimpl.RSSFeedParser;
import com.java.rssfeed.interfaces.IPageParser;

public class ApplicationTests {

	private final static String bseCorporateAnnBaseUrl = "http://www.bseindia.com/corporates/ann.aspx";
	public static String[][] feedInfo = {
//			{ "http://www.bseindia.com/data/xml/notices.xml", "buyback",
//					"buy back", "buy-back" },
//			{ "http://feeds.feedburner.com/nseindia/boardmeet", "buyback",
//					"buy back", "buy-back" },
			{ "http://feeds.feedburner.com/nseindia/ann", "buyback",
					"buy back", "buy-back" },
			{ "http://feeds.feedburner.com/nseindia/ca", "buyback", "buy back",
					"buy-back" },
			{ "http://feeds.feedburner.com/nseindia/results", "buyback",
					"buy back", "buy-back" },
			// { "https://capitalmind.in/tag/buyback/feed/", "buyback",
			// "buy back", "buy-back" },
			{ "http://www.sebi.gov.in/sebirss.xml", "buyback", "buy back",
					"buy-back" },
			{ "http://www.moneycontrol.com/rss/latestnews.xml", "buyback",
					"buy back", "buy-back" },
			{ "http://www.bseindia.com/corporates/ann.aspx?expandable=3",
					"buyback", "buy back", "buy-back" } };

	public static void main(String[] args) {
		int sleepDuration = 300000;
		// int sleepDuration = 30;
		while (true) {
			for (String[] feedLinkInfo : feedInfo) {
				try {
					IPageParser parser = getFeedParser(feedLinkInfo[0]);
					for (int filterIdx = 1; filterIdx < feedLinkInfo.length; filterIdx++) {
						parser.addFilterString(feedLinkInfo[filterIdx], true);
					}
//					Feed feed = FeedInfoStore.getInstance().getFeed(i);
//					Feed feed = parser.readFeed();
//					if (feed.getMessages().size() > 0) {
//						System.out.println("Feed from :" + feedLinkInfo[0]);
//					}
//					for (FeedMessage message : feed.getMessages()) {
//						System.out.println(message);
//					}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
			try {
				Thread.sleep(sleepDuration);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static IPageParser getFeedParser(String string) throws MalformedURLException {
		IPageParser parser = feedParserMap.get(string);
		if (parser == null) {
			if (string.startsWith(bseCorporateAnnBaseUrl)) {
				parser = new PageScrapBse(string);
			} else {
				parser = new RSSFeedParser(string);
			}
			feedParserMap.put(string, parser);
		}
		return parser;
	}

	private static Map<String, IPageParser> feedParserMap = new HashMap<String, IPageParser>();


	@Test
	public void addition_isCorrect() throws Exception {
		assertEquals(4, 2 + 2);
	}

	}


