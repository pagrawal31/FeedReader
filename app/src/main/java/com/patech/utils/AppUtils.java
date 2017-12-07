package com.patech.utils;

import java.util.Date;

/**
 * Created by pagrawal on 11-11-2017.
 */

public class AppUtils {
    public static final String EMPTY = "";
    public static final String FEED_PREFIX = "feed/";
    public static final String FEEDLY_URL_PREFIX = "http://cloud.feedly.com/v3/search/feeds?n=20&q=";

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

}
