package com.patech.imexport.opml;

import org.jsoup.helper.StringUtil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Mohit on 26/11/17.
 */

public class OpmlParser {
    private static final String EMPTY_STRING = "";
    private static final String RFC822_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

    public static List<Outline> read(File opmlFile) {
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
            Subscription subscription = null;
            List<Subscription> subscriptionList = null;

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
                                    if (subscriptionList == null) {
                                        subscriptionList = new ArrayList<>();
                                    }

                                    nestedOutline = true;
                                } else {
                                    outline = new Outline();
                                    parsingOutline = true;
                                }

                                subscription = new Subscription();
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
                                if (attributes.containsKey("text")) {
                                    String outlineText = (String) attributes.get("text");

                                    if (!nestedOutline) {
                                        outline.setText(outlineText);
                                    }

                                    subscription.setText(outlineText);
                                }

                                if (attributes.containsKey("type")) {
                                    subscription.setType((String)attributes.get("type"));
                                }

                                if (attributes.containsKey("created")) {
                                    outline.setCreated(getDate((String)attributes.get("created")));
                                }

                                if (attributes.containsKey("category")) {
                                    outline.setCategory((String)attributes.get("category"));
                                }

                                if (attributes.containsKey("xmlUrl")) {
                                    subscription.setXmlUrl((String)attributes.get("xmlUrl"));
                                }

                                if (attributes.containsKey("htmlUrl")) {
                                    subscription.setHtmlUrl((String)attributes.get("htmlUrl"));
                                }

                                if (attributes.containsKey("description")) {
                                    subscription.setDescription((String)attributes.get("description"));
                                }

                                if (attributes.containsKey("title")) {
                                    subscription.setTitle((String)attributes.get("title"));
                                }

                                if (attributes.containsKey("language")) {
                                    subscription.setXmlUrl((String)attributes.get("language"));
                                }

                                if (nestedOutline) {
                                    subscriptionList.add(subscription);
                                    nestedOutline = false;
                                } else {
                                    outline.setSubscriptions(subscriptionList);
                                    subscription = null;
                                    subscriptionList = null;
                                    parsingOutline = false;

                                }
                        }
                        attributes.clear();

                        break;

                    default:
                }

                System.out.println(eventType);
                eventType = parser.next();
            }
        } catch (ParseException exception) {
            System.out.println("Exception: " + exception.getMessage());
        } catch (XmlPullParserException exception) {
            System.out.println("Exception: " + exception.getMessage());
        } catch (FileNotFoundException exception) {
            System.out.println("Exception: " + exception.getMessage());
        } catch (IOException exception) {
            System.out.println("Exception: " + exception.getMessage());
        } catch (RuntimeException exception) {
            System.out.println("Exception: " + exception.getMessage());
        }

        return new ArrayList<>();
    }

    public static void write(List<Outline> outlines, File outputFile) {
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
