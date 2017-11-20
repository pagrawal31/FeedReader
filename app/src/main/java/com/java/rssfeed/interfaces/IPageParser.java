package com.java.rssfeed.interfaces;

import java.util.Set;

import com.java.rssfeed.feed.Feed;
import com.java.rssfeed.feed.FeedMessage;

public interface IPageParser {
	
	public Set<FeedMessage> getMessages();
    public Feed readFeed();
    public Feed getHeaderFeed();
    public void addFilterString(String filterTxt, boolean isInclude);
    public void addFilter(IFeedFilter filter);
    public void removeFilter(IFeedFilter filter);
    public void removeFilterAll();
    public boolean filterFeedMessage(FeedMessage message);
}
