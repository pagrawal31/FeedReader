package com.patech.utils;

import java.text.Format;

/**
 * Created by pagrawal on 22-11-2017.
 */

public class CommonMsgs {


    public static final String UNKNOWN_ERROR = "Unknown Error occured";
    public static final String IO_ERROR = "Input Output Error";
    public static final String URL_NOT_VALID = "URL is not valid";
    public static final String PARSING_ERROR = "Parsing Error occured";

    // Feed related msgs
    public static final String ENTER_VALID_TEXT_TO_SEARCH = "Please enter text which is more than 3 characters";
    public static final String FEED_ALREADY_EXISTS = "This feed already exists";
    public static final String FEED_ADDED = "Feed added";
    public static final String TEXT_COPIED = "Text copied to clipboard";

    public static String getTextCopied(String txt) {
        return new String(TEXT_COPIED + " [" + txt + "] ");
    }


    // Filter related msgs


    // General msgs like internet not available, permission issue.

}
