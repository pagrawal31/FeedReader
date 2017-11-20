package com.java.rssfeed.parserimpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.java.rssfeed.feed.Feed;
import com.java.rssfeed.FeedInfoStore;
import com.java.rssfeed.feed.FeedMessage;
import com.java.rssfeed.interfaces.IPageParser;

import android.os.AsyncTask;

public class PageScrapBse extends AbstractPageParser implements IPageParser {

    Feed feed = null;
    final Pattern ptnContent = Pattern.compile("<span id=\"ctl00_ContentPlaceHolder1_lblann\">(.+?)</span>");
    final Pattern ptnNextPage = Pattern.compile("<span id=\"ctl00_ContentPlaceHolder1_lblNext\" class=\"annlnk04\">(.+?)</span>");
    final String feedUrl;
    private static Date latestDate = null;
    private static Date currAtmostDate = null;
    private static Date currDate = null;
    static Feed feedInfo = null;
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    public PageScrapBse(String feedUrl) {
        this.feedUrl = feedUrl;
    }
    
    /*
     * <span id="ctl00_ContentPlaceHolder1_lblNext" class="annlnk04"><a class="tablebluelink" href="http://www.bseindia.com/corporates/ann.aspx?curpg=161&amp;annflag=1&amp;dt=20170606&amp;dur=D&amp;dtto=&amp;cat=&amp;scrip=&amp;anntype=C">Next &gt;&gt;</a></span>
     */
    public String scrapPageContent(String content) {
        
        String nextPageContent = null;
        String nextPageUrl = "";
        
        final Matcher matcherNextPage = ptnNextPage.matcher(content);
        if (matcherNextPage.find()) {
            nextPageContent = matcherNextPage.group(1);
            nextPageUrl = getUrlFromContent(nextPageContent);
        }
        
        final Matcher matcher = ptnContent.matcher(content);
        String tableContent = null;
        if (matcher.find()) {
            tableContent = matcher.group(1);
        }
        
        if (!updateFeed(tableContent))
            return null;
        return nextPageUrl;
    }
    
    private String getUrlFromContent(String nextPageContent) {
        String url = "";
        try {
            org.jsoup.nodes.Document document = Jsoup.parse(nextPageContent);
            Elements bodyElements = document.getElementsByTag("body");
            for (org.jsoup.nodes.Element element : bodyElements) {
                url = element.childNode(0).attributes().get("href");
            }
            
        } catch (Exception e) {
            
        }
        return url;
    }
    
    /*
     * returns false if the feeds are older then the cached feeds
     *          otherwise true.
     */
    private boolean updateFeed(String tableContent) {
        try {
        	this.feedInfo = feed;
            org.jsoup.nodes.Document document = Jsoup.parse(tableContent);
            Elements tblElements = document.getElementsByTag("table");
            int counter = 0;
            boolean first = false;
            String name = null;
            String title = null;
            String link = null;
            String pubDate = null;
            String endTime = null;
            String description = null;
            if (tblElements != null && !tblElements.isEmpty()) {
                for (org.jsoup.nodes.Element element : tblElements) {
                    for (Node node : element.childNodes()) {
                        for (Node trNode : node.childNodes()) {
                            if (!first) {
                                first = true;
                                continue; // first one is header.
                            }
                            int position = counter %4;
                            switch (position) {
                            case 0:
                                // tr contains three td here
                                // first is company name
                                // second is Type of update
                                // third is link of pdf
                                name = cleanupHtmlMarkup(trNode.childNode(0).childNode(0).toString());
                                title = cleanupHtmlMarkup(trNode.childNode(1).childNode(0).toString());
                                link = trNode.childNode(2).childNode(0).attributes().get("href");
                                break;
                            case 1:
                                Node dateInfoNode = trNode.childNode(0);
                                Node dateNode = getDateNode(dateInfoNode);
                                
                                pubDate = cleanupHtmlMarkup(dateNode.toString());
                                try {
                                	currDate = DATE_FORMATTER.parse(pubDate);
                                    if (currAtmostDate == null) {
                                        currAtmostDate = currDate;
                                    }
                                }
                                catch (Exception e) {
                                    currDate = null;
                                }
                                if (compareDates(latestDate, currDate)) {
                                    // no need to check feeds further.
                                    return false;
                                }
//                                endTime = dateInfoNode.childNode(3).toString().replaceAll("&nbsp;", "");
                                break;
                            case 2:
                                description = trNode.childNode(0).childNode(0).toString();
                                break;
                            case 3:
                                FeedMessage message = new FeedMessage();
                                message.setAuthor(name);
                                message.setDescription(description);
                                message.setLink(link);
                                message.setTitle(title);
                                message.setDate(pubDate);
                                
								if (!feedSet.contains(message)) {
									feed.getMessages().add(message);
									feedSet.add(message);
								}
                                
                                // add message to feed here.
                                {
                                    message = null;
                                    name = "";
                                    title = "";
                                    link = "";
                                    description = "";
                                    pubDate = "";
                                }
                                break;
                            }
                            counter++;
                        }
                    }
                }
            }
            
    } catch (Exception e) {
        e.printStackTrace();
    }
        return true;
    }

    private boolean compareDates(Date latestDate, Date currDate) {
        if (latestDate == null || currDate == null)
            return false;
        if (latestDate.after(currDate))
            return true;
        return false;
    }

    private String cleanupHtmlMarkup(String string) {
        String cleanString = string.replaceAll("&nbsp;", "");
        cleanString = cleanString.replaceAll("&amp;nbsp", "");
        return cleanString;
    }

//    public static void main(String[] args) {
//        PageScrapBse bsePage = new PageScrapBse("http://www.bseindia.com/corporates/ann.aspx?expandable=3");
//        bsePage.addFilterString("buyback");
//        bsePage.addFilterString("buy back");
//        
//        String url = bsePage.feedUrl;
//        
//        while (true) {
//            while (url != null && !url.isEmpty()) {
//                if (!url.startsWith("http")) {
//                    url = "http://www.bseindia.com/corporates/" + url;
//                }
//                String buffer = extractWebpage(url);
//                if (buffer != null && !buffer.isEmpty())
//                    url = bsePage.scrapPageContent(buffer);
//                else
//                    System.out.println("Buffer is empty");
//            }
//        }
//    }
    
	public static String extractWebpage(String url)
			throws MalformedURLException, IOException {

		String webpage = null;
		System.setProperty("http.agent", "");
		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

		BufferedReader br = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuffer webpageBuffer = new StringBuffer();

		String inputLine;
		while ((inputLine = br.readLine()) != null) {
			webpageBuffer.append(inputLine);
		}
		br.close();
		webpage = webpageBuffer.toString();
		return webpage;
	}

    public static String testFromFile() {
        
        String file = "C:\\Users\\pagrawal\\Desktop\\buyback\\bsePage\\extractedData.html";
        file = "C:\\Users\\pagrawal\\Desktop\\buyback\\bsePage\\Latest Stock_Share Market Updates _ Corporate News _ Announcements _ BSE.html";
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    @Override
    public Feed readFeed() {
    	read();
    	return null;
    }
    

	private void read() {
       new HttpGetTask().execute(feedUrl);		
	}

	@Override
	public Set<FeedMessage> getMessages() {
		return feedSet;
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
				feed = new Feed("Bse Corporate Filing", "http://www.bseindia.com/corporates/", null, null, null, null);
		        String originalUrl = url[0];
		        while (originalUrl != null && !originalUrl.isEmpty()) {
		            if (!originalUrl.startsWith("http")) {
		                originalUrl ="http://www.bseindia.com/corporates/" + originalUrl;
		            }
		            String pageContent = extractWebpage(originalUrl);
		            if (pageContent != null && !pageContent.isEmpty())
		                originalUrl = scrapPageContent(pageContent);
		        }
		        if (currAtmostDate != null)
		            latestDate = currAtmostDate;
		        currAtmostDate = null;
				return url[0];
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return currUrl;
		}

		@Override
		protected void onPostExecute(String url) {
			FeedInfoStore.releseHandle(url);
		}
	}
	
	private Node getDateNode(Node dateInfoNode) {
        List<Node> children = dateInfoNode.childNodes();
        Node dateNode = null;
        if (children.size() == 3) {
            dateNode = children.get(2);
        } else if (children.size() == 6) {
            dateNode = children.get(3);
        } else {
            dateNode = dateInfoNode;
        }
        return dateNode;
    }

	@Override
	public Feed getHeaderFeed() {
		return this.feedInfo;
	}

}
