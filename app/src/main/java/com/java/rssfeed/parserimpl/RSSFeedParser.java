package com.java.rssfeed.parserimpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.java.rssfeed.feed.Feed;
import com.java.rssfeed.FeedInfoStore;
import com.java.rssfeed.feed.FeedMessage;
import com.java.rssfeed.interfaces.IPageParser;
import com.patech.utils.AppUtils;

import android.os.AsyncTask;

public class RSSFeedParser extends AbstractPageParser implements IPageParser {
    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String CHANNEL = "channel";
    static final String LANGUAGE = "language";
    static final String COPYRIGHT = "copyright";
    static final String LINK = "link";
    static final String AUTHOR = "author";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String GUID = "guid";
    String currentPageData = null;
    Feed feedInfo = null;

    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    private Date currAtmostDate = null;
    private Date currDate = null;
    private Date latestDate = null;

    final URL url;
    final String feedUrl;

    public RSSFeedParser(String feedUrl) throws MalformedURLException {
        try {
        	this.feedUrl = feedUrl;
            this.url = new URL(feedUrl);
        } catch (MalformedURLException e) {
            throw e;
        }
    }

    public Feed readFeedFromData(InputStream in) {
		Feed feed = null;
		boolean isFeedHeader = true;
		// Set header values intial to the empty string
		String description = "";
		String title = "";
		String link = "";
		String language = "";
		String copyright = "";
		String author = "";
		String pubDate = "";
		String guid = "";

		XmlPullParserFactory factory;
		try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(in, null);

            int eventType = parser.getEventType();

            String text = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {

                String tagName = parser.getName();
                switch (eventType) {
                case XmlPullParser.START_TAG:
                    switch (tagName) {
                    case ITEM:
                        if (isFeedHeader) {
                            isFeedHeader = false;
                            feed = new Feed(title, link, description, language, copyright, pubDate);
                            this.feedInfo = feed;
                        }
                        break;
                    default:
                        break;
                    }
                    break;
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    switch (tagName) {
                    case ITEM:
                        FeedMessage message = new FeedMessage();
                        message.setAuthor(author);
                        message.setDescription(description);
                        message.setGuid(guid);
                        message.setLink(link);
                        message.setTitle(title);
                        message.setDate(pubDate);
                        if (!feedSet.contains(message)) {
                            feed.getMessages().add(message);
                            feedSet.add(message);
                        }
                        {
                            description = "";
                            title = "";
                            link = "";
                            language = "";
                            copyright = "";
                            author = "";
                            pubDate = "";
                            guid = "";
                        }
                        eventType = parser.next();
                        break;
                    case TITLE:
                        title = text;
                        break;
                    case DESCRIPTION:
                        description = text;
                        break;
                    case LINK:
                        link = text;
                        break;
                    case GUID:
                        guid = text;
                        break;
                    case LANGUAGE:
                        language = text;
                        break;
                    case AUTHOR:
                        author = text;
                        break;
                    case PUB_DATE:
                        pubDate = text;

                        try {
                            currDate = DATE_FORMATTER.parse(pubDate);
                            if (currAtmostDate == null) {
                                currAtmostDate = currDate;
                            }
                        }
                        catch (Exception e) {
                            currDate = null;
                        }

                        if (AppUtils.compareDates(latestDate, currDate)) {
                            // no need to check feeds further.
                            return feed;
                        }
                        break;
                    case COPYRIGHT:
                        copyright = text;
                        break;
                    }
                    break;
                }
                eventType = parser.next();
            }
		} catch (XmlPullParserException pullParserException) {
			pullParserException.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return feed;
    	
    }
    @Override
	public Feed readFeed() {
		read();
		return null;
	}

	private void read() {
		new HttpGetTask().execute(feedUrl);
	}
    
	private class HttpGetTask extends AsyncTask<String, Void, String> {
		
		private static final String TAG = "HttpGet";

		@Override
		protected String doInBackground(String... url) {
			String currUrl = url[0];
			try {
				if (!FeedInfoStore.requestHandle(currUrl)) {
					return currUrl;
				}
				System.setProperty("http.agent", "");
				URL obj = new URL(currUrl);
				HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
				readFeedFromData(conn.getInputStream());

				if (currAtmostDate != null)
                    latestDate = currAtmostDate;
                currAtmostDate = null;

				return currUrl;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
                FeedInfoStore.releseHandle(currUrl);
            }
            return currUrl;
		}

		@Override
		protected void onPostExecute(String url) {
			FeedInfoStore.releseHandle(url);
		}
	}

	@Override
	public Set<FeedMessage> getMessages() {
		return feedSet;
	}

	@Override
	public Feed getHeaderFeed() {
		return this.feedInfo;
	}
}
