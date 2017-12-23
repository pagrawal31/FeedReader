package com.java.rssfeed.interfaces;

import java.util.Set;

import com.java.rssfeed.feed.Feed;
import com.java.rssfeed.feed.FeedMessage;

public interface IPageParser {

    public void readFeed(Feed feed);
    public Feed getHeaderFeed();
    public void addFilterString(String filterTxt, boolean isInclude);
    public void addFilter(IFeedFilter filter);
    public void removeFilter(IFeedFilter filter);
    public void removeFilterAll();
    public void cleanUpFeedMsgs(Feed feedInfo);
    public Set<IFeedFilter> getFilters();
    public boolean filterFeedMessage(FeedMessage message);
    public boolean filterFeedMessageExcluded(FeedMessage message);
}
