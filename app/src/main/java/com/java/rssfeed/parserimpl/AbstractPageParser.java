package com.java.rssfeed.parserimpl;

import com.java.rssfeed.feed.FeedMessage;
import com.java.rssfeed.filterimpl.ExcludeFeedFilter;
import com.java.rssfeed.filterimpl.IncludeFeedFilter;
import com.java.rssfeed.interfaces.IFeedFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbstractPageParser {
	
	public Set<IFeedFilter> filters;
	public Set<FeedMessage> feedSet;
    
	public AbstractPageParser() {
        this.filters = new HashSet<>();
        this.feedSet = new LinkedHashSet<>();
    }
    public boolean filterFeedMessage(FeedMessage message) {
        Map<String, Boolean> filterResultMap = new HashMap<>();
        for (IFeedFilter filter : filters) {
            String className = filter.getClass().getSimpleName();
            boolean currResult = filterIt(message, filter);
            Boolean soFarResult = filterResultMap.get(className);
            if (soFarResult != null) {
                filterResultMap.put(className, filter.mergeResult(soFarResult, currResult));
            } else {
                filterResultMap.put(className, currResult);
            }
        }
        boolean finalResult = false;
        for (Boolean result : filterResultMap.values()) {
            if (!result)
                return false;
            finalResult = result;
        }
        return finalResult;
    }

    public boolean filterIt(FeedMessage message, IFeedFilter filter) {
        return filter.filterIt(message);
    }

    public void addFilterString(String filterTxt, boolean isInclude) {
        if (filterTxt == null || filterTxt.isEmpty())
            return;

        IFeedFilter filter;
        if (isInclude) {
            filter = new IncludeFeedFilter(filterTxt.toLowerCase());
        } else {
            filter = new ExcludeFeedFilter(filterTxt.toLowerCase());
        }
        addFilter(filter);
    }

    public void addFilter(IFeedFilter filter) {
        filters.add(filter);
    }

    public void removeFilter(IFeedFilter filter) {
        filters.remove(filter);
    }

    public void removeFilterAll() {
        filters.clear();
    }


}
