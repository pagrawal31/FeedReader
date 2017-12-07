package com.java.rssfeed.filterimpl;

import com.java.rssfeed.feed.FeedMessage;
import com.java.rssfeed.filterimpl.FeedFilter;

/**
 * Created by pagrawal on 21-10-2017.
 */

public class ExcludeFeedFilter extends FeedFilter {

    private static final String FILTERTYPE = "ExcludeFilter";

    @Deprecated
    public ExcludeFeedFilter(String filterTxt) {
        this(filterTxt, null, null, false) ;
    }

    public ExcludeFeedFilter(String filterTxt, String name, String description, boolean isGlobalFilter) {
        super(filterTxt, name, description, true, isGlobalFilter);
    }

    @Override
    public boolean _filterIt(FeedMessage msg) {
        return !msg.containsTxt(this.getFilterText().toLowerCase());
    }

    @Override
    public String getFilterType() {
        return FILTERTYPE;
    }

    @Override
    public Boolean mergeResult(Boolean soFarResult, boolean currResult) {
        if (soFarResult == null)
            return currResult;
        return soFarResult && currResult;
    }
}
