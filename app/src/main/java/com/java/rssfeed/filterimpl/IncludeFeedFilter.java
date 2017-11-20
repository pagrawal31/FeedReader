package com.java.rssfeed.filterimpl;

import com.java.rssfeed.feed.FeedMessage;
import com.java.rssfeed.filterimpl.FeedFilter;

/**
 * Created by pagrawal on 21-10-2017.
 */

public class IncludeFeedFilter extends FeedFilter {

    public static final String FILTERTYPE = "IncludeFilter";

    public IncludeFeedFilter(String filterTxt) {
        this(filterTxt, null, null);
    }

    public IncludeFeedFilter(String filterTxt, String name, String description) {
        super(filterTxt, name, description, false);
    }

    @Override
    public boolean _filterIt(String msg) {
        return msg.contains(this.getFilterText());
    }


    @Override
    public boolean filterIt(FeedMessage msg) {
        return (filterIt(msg.getTitle()) || filterIt(msg.getDescription()) || filterIt(msg.getAuthor()));
    }

    @Override
    public String getFilterType() {
        return FILTERTYPE;
    }
}
