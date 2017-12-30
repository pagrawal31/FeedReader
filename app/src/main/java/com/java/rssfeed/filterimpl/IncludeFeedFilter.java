package com.java.rssfeed.filterimpl;

import com.java.rssfeed.model.feed.FeedMessage;

/**
 * Created by pagrawal on 21-10-2017.
 */

public class IncludeFeedFilter extends FeedFilter {

    public static final String FILTERTYPE = "IncludeFilter";

    public IncludeFeedFilter(String filterTxt) {
        this(filterTxt, null, null, false);
    }

    public IncludeFeedFilter(String filterTxt, String name, String description, boolean isGlobalFilter) {
        super(filterTxt, name, description, false, isGlobalFilter);
    }

    @Override
    public boolean _filterIt(FeedMessage msg) {
        return msg.containsTxt(this.getFilterText().toLowerCase());
    }

    @Override
    public String getFilterType() {
        return FILTERTYPE;
    }

    @Override
    public Boolean mergeResult(Boolean soFarResult, boolean currResult) {
        if (soFarResult == null)
            return currResult;
        return soFarResult || currResult;
    }
}
