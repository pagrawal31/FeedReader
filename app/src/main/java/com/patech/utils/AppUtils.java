package com.patech.utils;

import com.java.rssfeed.feed.Feed;
import com.java.rssfeed.feed.FeedMessage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by pagrawal on 11-11-2017.
 */

public class AppUtils {
    public static final String EMPTY = "";
    public static final String FEED_PREFIX = "feed/";
    public static final String FEEDLY_URL_PREFIX = "http://cloud.feedly.com/v3/search/feeds?n=20&q=";

    public static final DateFormat STANDARD_DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public static final DateFormat RSS_DATE_FORMATTER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    public static final String[] EMPTY_SELECTION = new String[]{};

    public static int getIntFromBoolean(boolean value) {
        return value ? 1 : 0;
    }

    public static boolean getBooleanFromInt(int value) {
        return value == 1 ? true : false;
    }

    public static boolean compareDates(Date latestDate, Date currDate) {
        if (latestDate == null || currDate == null)
            return false;
        if (latestDate.after(currDate))
            return true;
        return false;
    }

    public static Date parseDate(String date) {
        Date resultDate = null;
        try {
            resultDate = STANDARD_DATE_FORMATTER.parse(date);
        } catch (ParseException pe) {
        }
        return resultDate;
    }

    public static class Compare implements Comparator<FeedMessage> {
        public int compare(FeedMessage msg1, FeedMessage msg2) {
            Date date1 = AppUtils.parseDate(msg1.getDate());
            Date date2 = AppUtils.parseDate(msg2.getDate());
            if (date1 == null || date2 == null)
                return 0;
            return date2.compareTo(date1);
        }
    }

}
