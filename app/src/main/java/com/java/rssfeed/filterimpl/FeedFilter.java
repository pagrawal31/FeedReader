package com.java.rssfeed.filterimpl;

import com.java.rssfeed.model.feed.FeedMessage;
import com.patech.utils.AppUtils;
import com.java.rssfeed.interfaces.IFeedFilter;

/**
 * Created by pagrawal on 21-10-2017.
 */

public abstract class FeedFilter implements IFeedFilter {

    private final boolean DEFAULT;
    private final String filterTxt;
    private final String name;
    private final String description;
    private final boolean isGlobalFilter;

    public FeedFilter(String filterTxt, String name, String description, boolean valueIfNull, boolean isGlobalFilter) {
        if (filterTxt == null || filterTxt.isEmpty()) {
            this.filterTxt = AppUtils.EMPTY;
        } else {
            this.filterTxt = filterTxt.toLowerCase();
        }
        this.isGlobalFilter = isGlobalFilter;
        this.name = name;
        this.description = description;
        DEFAULT = valueIfNull;
    }

    @Override
    public String getFilterText() {
        return this.filterTxt;
    }

    @Override
    public String getFilterDesc() {
        return description;
    }

    @Override
    public String getFilterName() {
        return name;
    }

    public boolean filterIt(FeedMessage msg) {
        if (msg == null || msg.isEmpty() || filterTxt == AppUtils.EMPTY)
            return DEFAULT;
        return _filterIt(msg);
    }

    @Override
    public int hashCode() {
        return filterTxt.hashCode() + getFilterType().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        FeedFilter otherFilter = (FeedFilter) obj;
        return ((otherFilter.getFilterText().equalsIgnoreCase(this.getFilterText())) &&
                (otherFilter.getFilterType() == this.getFilterType()));
    }

    protected abstract boolean _filterIt(FeedMessage msg);

    public boolean isGlobalFilter() {
        return isGlobalFilter;
    }
}
