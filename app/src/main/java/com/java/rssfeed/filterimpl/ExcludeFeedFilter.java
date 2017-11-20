package com.java.rssfeed.filterimpl;

import com.java.rssfeed.feed.FeedMessage;
import com.java.rssfeed.filterimpl.FeedFilter;

/**
 * Created by pagrawal on 21-10-2017.
 */

public class ExcludeFeedFilter extends FeedFilter {

    private static final String FILTERTYPE = "ExcludeFilter";

    public ExcludeFeedFilter(String filterTxt) {
        this(filterTxt, null, null) ;
    }

    public ExcludeFeedFilter(String filterTxt, String name, String description) {
        super(filterTxt, name, description, true);
    }

    @Override
    public boolean _filterIt(String msg) {
        return !msg.contains(this.getFilterText());
    }

    @Override
    public boolean filterIt(FeedMessage msg) {
        return (filterIt(msg.getTitle()) && filterIt(msg.getDescription()) && filterIt(msg.getAuthor()));
    }

    @Override
    public String getFilterType() {
        return FILTERTYPE;
    }
}
