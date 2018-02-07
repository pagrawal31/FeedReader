package com.patech.imexport.opml;

import android.util.Xml;

import com.java.rssfeed.model.feed.Feed;
import com.java.rssfeed.model.feed.OPML;
import com.java.rssfeed.model.feed.Outline;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import static com.patech.utils.AppUtils.isEmpty;

/**
 * Created by Mohit on 26/11/17.
 */

public class OpmlParser {
    enum OPMLTag {

        OUTLINE("outline"),
        HEAD("head"),
        BODY("body"),
        TYPE("type"),
        CATEGORY("category"),
        CREATED("created"),
        HTMLURL("htmlUrl"),
        DESCRIPTION("description"),
        TITLE("title"), LANGUAGE("language"), COPYRIGHT("copyright"), TEXT("text"), XMLURL("xmlUrl"), OPML("opml");

        String name;
        OPMLTag(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    private static final String EMPTY_STRING = "";
    private static final String RFC822_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

    public static List<Outline> read(File opmlFile) {
        List<Outline> outlineList = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(new FileReader(opmlFile));
            int eventType = parser.getEventType();

            String tagName = EMPTY_STRING;
            String text = EMPTY_STRING;
            OPML opml = null;
            Outline outline = null;
            Map<String, Object> attributes = new HashMap<>();
            Boolean parsingOutline = false;
            Boolean nestedOutline = false;
            Feed feed = null;
            Feed.FeedBuilder feedBuilder = null;
            List<Feed> subscriptionList = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        attributes = getAttributes(parser);

                        switch (tagName) {
                            case "opml":
                                opml = new OPML();
                                break;

                            case "outline":
                                if (parsingOutline) {
                                    nestedOutline = true;
                                } else {
                                    if (subscriptionList == null) {
                                        subscriptionList = new ArrayList<>();
                                    }

                                    outline = new Outline();
                                    parsingOutline = true;
                                }

                                feed = null;
                                feedBuilder = new Feed.FeedBuilder();
                                break;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (opml == null) {
                            // XML not well formed
                            throw new RuntimeException("OPML is not well-formed");
                        }

                        switch (tagName) {
                            case "title":
                                opml.setTitle(text);
                                break;

                            case "dateCreated":
                                opml.setCreated(getDate(text));
                                break;

                            case "modifiedDate":
                                opml.setModified(getDate(text));
                                break;

                            case "ownerName":
                                opml.setOwnerName(text);
                                break;

                            case "onwnerEmail":
                                opml.setOwnerEmail(text);
                                break;

                            case "docs":
                                opml.setDocs(text);
                                break;

                            case "outline":
                                if (attributes.containsKey(OPMLTag.TEXT.getName())) {
                                    String outlineText = (String) attributes.get(OPMLTag.TEXT.getName());

                                    if (!nestedOutline) {
                                        outline.setText(outlineText);
                                    }

                                    feedBuilder.setDescription(outlineText);
                                }

                                if (attributes.containsKey(OPMLTag.TYPE.getName())) {
                                    feedBuilder.setType((String)attributes.get(OPMLTag.TYPE.getName()));
                                }

                                if (attributes.containsKey(OPMLTag.CREATED.getName())) {
                                    outline.setCreated(getDate((String)attributes.get(OPMLTag.CREATED.getName())));
                                }

                                if (attributes.containsKey(OPMLTag.CATEGORY.getName())) {
                                    outline.setCategory((String)attributes.get(OPMLTag.CATEGORY.getName()));
                                }

                                if (attributes.containsKey(OPMLTag.HTMLURL.getName())) {
                                    feedBuilder.setHtmlUrl((String)attributes.get(OPMLTag.HTMLURL.getName()));
                                }

                                if (attributes.containsKey(OPMLTag.DESCRIPTION.getName())) {
                                    feedBuilder.setDescription((String)attributes.get(OPMLTag.DESCRIPTION.getName()));
                                }

                                if (attributes.containsKey(OPMLTag.TITLE.getName())) {
                                    feedBuilder.setTitle((String)attributes.get("title"));
                                }

                                if (attributes.containsKey(OPMLTag.LANGUAGE.getName())) {
                                    feedBuilder.setLanguage((String)attributes.get(OPMLTag.LANGUAGE.getName()));
                                }

                                if (attributes.containsKey(OPMLTag.XMLURL.getName())) {
                                    feed = feedBuilder.build((String)attributes.get(OPMLTag.XMLURL.getName()));
                                }
                                if (feed != null && subscriptionList != null)
                                    subscriptionList.add(feed);

                                if (nestedOutline) {
                                    nestedOutline = false;
                                } else {
                                    if (subscriptionList != null) {
                                        outline.setSubscriptions(subscriptionList);
                                        feed = null;
                                        subscriptionList = null;
                                        parsingOutline = false;
                                        outlineList.add(outline);
                                    }
                                }
                                break;

                            case "opml":
                                opml.setOutlines(outlineList);
                                break;
                        }
                        attributes.clear();

                        break;

                    default:
                }

//                System.out.println(eventType);
                eventType = parser.next();
            }
        } catch (ParseException | XmlPullParserException | RuntimeException  | IOException exception) {
            System.out.println("Exception: " + exception.getMessage());
        }
        return outlineList;
    }

    public static void write(List<Outline> outlines, File outputFile) throws FileNotFoundException, XmlPullParserException {

        FileOutputStream fos = new  FileOutputStream(outputFile);

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        XmlSerializer xmlSerializer = factory.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, OPMLTag.OPML.getName());
            xmlSerializer.attribute(null, "version", "1.0");

            xmlSerializer.startTag(null, OPMLTag.HEAD.getName());
            xmlSerializer.startTag(null, OPMLTag.TITLE.getName());
            xmlSerializer.text("My Feeds");
            xmlSerializer.endTag(null, OPMLTag.TITLE.getName());
            xmlSerializer.endTag(null, OPMLTag.HEAD.getName());

            xmlSerializer.startTag(null, OPMLTag.BODY.getName());

            for (Outline outline : outlines) {
                xmlSerializer.startTag(null, OPMLTag.OUTLINE.getName());
                for (Feed feed : outline.getSubscriptions()) {
                    serialize(xmlSerializer, feed);
                }
                xmlSerializer.endTag(null, OPMLTag.OUTLINE.getName());
            }

            xmlSerializer.endTag(null, OPMLTag.BODY.getName());
            xmlSerializer.endTag(null, OPMLTag.OPML.getName());
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWriter = writer.toString();
            fos.write(dataWriter.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void serialize(XmlSerializer xmlSerializer, Feed feed) throws IOException {
        if (!isEmpty(feed.getCopyright())) {
            writeData(xmlSerializer, feed.getCopyright(), OPMLTag.COPYRIGHT.getName());
        }
        if (!isEmpty(feed.getTitle())) {
            writeData(xmlSerializer, feed.getTitle(), OPMLTag.TITLE.getName());
        }
        if (!isEmpty(feed.getText())) {
            writeData(xmlSerializer, feed.getText(), OPMLTag.TEXT.getName());
        }
        if (!isEmpty(feed.getDescription())) {
            writeData(xmlSerializer, feed.getDescription(), OPMLTag.DESCRIPTION.getName());
        }
        if (!isEmpty(feed.getType())) {
            writeData(xmlSerializer, feed.getType(), OPMLTag.TYPE.getName());
        }
        if (!isEmpty(feed.getHtmlUrl())) {
            writeData(xmlSerializer, feed.getHtmlUrl(), OPMLTag.HTMLURL.getName());
        }
        if (!isEmpty(feed.getLanguage())) {
            writeData(xmlSerializer, feed.getLanguage(), OPMLTag.LANGUAGE.getName());
        }
        if (!isEmpty(feed.getXmlUrl())) {
            writeData(xmlSerializer, feed.getXmlUrl(), OPMLTag.XMLURL.getName());
        }
    }

    private static void writeData(XmlSerializer xmlSerializer, String txt, String tag) throws IOException {
//        xmlSerializer.startTag(null, tag);
//        xmlSerializer.text(txt);
//        xmlSerializer.endTag(null, tag);
        xmlSerializer.attribute(null, tag, txt);
    }

    private static Date getDate (String rfd822Date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(RFC822_DATE_FORMAT, Locale.ENGLISH);
        return format.parse(rfd822Date);
    }

    private static Map<String, Object> getAttributes (XmlPullParser parser) {
        int attributeCount = parser.getAttributeCount();
        Map<String, Object> attributeMap = new HashMap<>();

        for (int i = 0; i < attributeCount; i++) {
            attributeMap.put(parser.getAttributeName(i), parser.getAttributeValue(i));
        }

        return attributeMap;
    }
}
