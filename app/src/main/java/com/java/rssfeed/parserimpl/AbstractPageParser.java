package com.java.rssfeed.parserimpl;

import com.java.rssfeed.feed.Feed;
import com.java.rssfeed.feed.FeedMessage;
import com.java.rssfeed.filterimpl.ExcludeFeedFilter;
import com.java.rssfeed.filterimpl.IncludeFeedFilter;
import com.java.rssfeed.interfaces.IFeedFilter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbstractPageParser {
	
	public Set<IFeedFilter> filters;

	// currFeedSet contents the msgs for previous parsing so that the duplicate elements can be checked.
	protected Set<FeedMessage> currFeedSet;
    
	public AbstractPageParser() {
        this.filters = new HashSet<>();
        this.currFeedSet = new HashSet<>();
    }
    public Set<IFeedFilter> getFilters() {
	    return filters;
    }

    public boolean filterFeedMessageExcluded(FeedMessage message) {

        boolean soFarResult = true;
        for (IFeedFilter filter : filters) {
            if (filter instanceof IncludeFeedFilter)
                continue;

            boolean currResult = filterIt(message, filter);
            soFarResult = filter.mergeResult(soFarResult, currResult);
        }

        return soFarResult;
    }

    public boolean filterFeedMessage(FeedMessage message) {
        Map<String, Boolean> filterResultMap = new HashMap<>();

        // If there is only one exclude filter then all msgs are shown, this should not happen
        //
        filterResultMap.put(IncludeFeedFilter.class.getSimpleName(), false);

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

    public void cleanUpFeedMsgs(Feed feedInfo) {
	    currFeedSet.clear();
        feedInfo.deleteAllMsgs();
    }

    public void removeFilterAll() {
        filters.clear();
    }

    protected void updateFeedDate(Feed feedInfo, String latestDate) {
	    feedInfo.setLastUpdated(latestDate);
    }

    protected void updateFeedScannedDate(Feed feedInfo, String latestDate) {
        feedInfo.setLastScanned(latestDate);
    }

    protected void clearExistingSet(Set<FeedMessage> feedSet, List<FeedMessage> localEntries) {
        if (!localEntries.isEmpty()) {
            feedSet.clear();
            feedSet.addAll(localEntries);
        }
    }

}
