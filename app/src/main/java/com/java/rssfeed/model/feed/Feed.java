package com.java.rssfeed.model.feed;

import com.patech.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Feed {

    // xmlUrl points to the feed url of the original website
    private String xmlUrl;
    private String type;
    private String text;

    // htmlUrl generally points to the original website
    private String htmlUrl;
    private String name;
    private String description;
    private String language;
    private String copyright;
    private String pubDate;
    private String created;
    private String version; // we only support 2.0

    private String lastUpdated = "NA";
    private String lastScanned = "NA";

    final List<FeedMessage> entries = new ArrayList<>();

    public Feed(FeedBuilder builder) {
        this.name = builder.name;
        this.xmlUrl = builder.url;
        this.description = builder.description;
        this.language = builder.language;
        this.copyright = builder.copyright;
        this.pubDate = builder.pubDate;
        this.type = builder.type;
        this.htmlUrl = builder.htmlUrl;
    }

    public void setLastUpdated(String updatedOn) {
        this.lastUpdated = updatedOn;
    }
    public String getLastUpdated() {
        return this.lastUpdated;
    }

    public String getLastScanned() {
        return lastScanned;
    }

    public void setLastScanned(String lastScanned) {
        this.lastScanned = lastScanned;
    }

    public static class FeedBuilder {
        private String name = AppUtils.EMPTY;
        private String url = AppUtils.EMPTY;
        private String type = AppUtils.EMPTY;
        private String description = AppUtils.EMPTY;
        private String language = AppUtils.EMPTY;
        private String copyright = AppUtils.EMPTY;
        private String pubDate = AppUtils.EMPTY;
        private String htmlUrl = AppUtils.EMPTY;
        public FeedBuilder() {
        }

        public FeedBuilder setName(String name) {
            this.name = name;
            return this;
        }
        public FeedBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public FeedBuilder setPubDate(String pubDate) {
            this.pubDate = pubDate;
            return this;
        }

        public FeedBuilder setLanguage(String language) {
            this.language = language;
            return this;
        }

        public FeedBuilder setCopyright(String copyright) {
            this.copyright = copyright;
            return this;
        }
        public Feed build(String url) {
            this.url = url;
            Feed newFeed = new Feed(this);
            return newFeed;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setHtmlUrl(String htmlUrl) {
            this.htmlUrl = htmlUrl;
        }
    }

    public List<FeedMessage> getMessages() {
        return entries;
    }

    public String getLink() {
        return xmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }
    public String getCopyright() {
        return copyright;
    }

    public String getPubDate() {
        return pubDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        Feed otherFeed = (Feed) obj;
        return otherFeed.getLink().equals(getLink());
    }

    @Override
    public int hashCode() {
        return this.xmlUrl.hashCode();
    }

    @Override
    public String toString() {
        return "Feed [copyright=" + copyright + ", description=" + description
                + ", language=" + language + ", link=" + this.xmlUrl + ", pubDate="
                + pubDate + "]";
    }

    /*
        returns true if the feed is valid.
        As of now checks if url starts with http// or https://
     */
    public boolean isValid() {
        return true;
    }

    public void deleteAllMsgs() {
        entries.clear();
    }

}
