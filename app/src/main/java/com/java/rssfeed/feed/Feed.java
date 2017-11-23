package com.java.rssfeed.feed;

import com.patech.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class Feed {
    final String name;
    final String title;
    final String link;
    final String description;
    final String language;
    final String copyright;
    final String pubDate;
    
    final List<FeedMessage> entries = new ArrayList<FeedMessage>();

    public Feed(String name, String title, String link, String description, String language,
        String copyright, String pubDate) {
        this.name = name;
        this.title = title;
        this.link = link;
        this.description = description;
        this.language = language;
        this.copyright = copyright;
        this.pubDate = pubDate;
    }
    public static class FeedBuilder {
        private String name;
        private String title;
        private String url;
        private String description;
        private String language;
        private String copyright;
        private String pubDate;

        public FeedBuilder() {
            this.url = CommonUtils.EMPTY;
            this.name = CommonUtils.EMPTY;
            this.title = CommonUtils.EMPTY;
            this.description = CommonUtils.EMPTY;
            this.language = CommonUtils.EMPTY;
            this.copyright = CommonUtils.EMPTY;
            this.pubDate = CommonUtils.EMPTY;
        }

        public FeedBuilder setName(String name) {
            this.name = name;
            return this;
        }
        public FeedBuilder setTitle(String title) {
            this.title = title;
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
            Feed newFeed = new Feed(this.title, this.url, this.description, this.language, this.copyright,
                    this.pubDate);
            return newFeed;
        }
    }
    
    public Feed(String title, String link, String description, String language,
            String copyright, String pubDate) {
        this(null, title, link, description, language, copyright, pubDate);
    }
    public List<FeedMessage> getMessages() {
        return entries;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
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
        return this.link.hashCode();
    }

    @Override
    public String toString() {
        return "Feed [copyright=" + copyright + ", description=" + description
                + ", language=" + language + ", link=" + link + ", pubDate="
                + pubDate + ", title=" + title + "]";
    }

    /*
        returns true if the feed is valid.
        As of now checks if url starts with http// or https://
     */
    public boolean isValid() {
        return true;
    }

}
