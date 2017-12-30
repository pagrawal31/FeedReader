package com.java.rssfeed.interfaces;

import com.java.rssfeed.model.feed.FeedMessage;

public interface IFeedFilter {
    // allow based on filter or disallow ?
    // contains or does not contains ?
    // starts with or ends with
    // isRegex ?

    public String getFilterText();
    public String getFilterName();
    public String getFilterDesc();
    public String getFilterType();
    public boolean isGlobalFilter();

    public boolean filterIt(FeedMessage msg);

    Boolean mergeResult(Boolean soFarResult, boolean currResult);
}
