package com.java.rssfeed.feed;

import java.util.zip.CRC32;

import static com.patech.utils.CommonUtils.EMPTY;

public class FeedMessage {
    private String title = EMPTY;
    private String description = EMPTY;
    private String link = EMPTY;
    private String author = EMPTY;
    private String guid = EMPTY;
    private String date = EMPTY;
    private int crc32 = -1;
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title : EMPTY;
    }

    public String getDescription() {
        return description;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author != null ? author : EMPTY;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description != null ? description :  EMPTY;
    }
    
    @Override
    public int hashCode() {
        return getId();
    }
    
    @Override
    public boolean equals(Object obj) {
        return getId() == obj.hashCode();
    }
    
    /*
     * for unique ID calculation.
     */
    public int getId() {
        if (this.link == null || this.link.isEmpty())
            return -1;
        if (crc32 != -1)
            return crc32;
        CRC32 crc = new CRC32();
        crc.update(this.link.getBytes());
        crc32 = (int) crc.getValue();
        return crc32;
    }
    
    @Override
    public String toString() {
//        return "FeedMessage [title=" + title + "\n, description=" + description
//                + "\n, link=" + link + "\n, author=" + author + ", guid=" + guid
//                + ", date=" + date + "]";
        return displayString();
    }
    
    public String displayString() {
    	return author.isEmpty() ? title : author + " - " + title; 
    }

    public void setDate(String pubDate) {
        this.date = pubDate;
    }
    
    public String getDate() {
        return this.date;
    }

    public boolean isEmpty() {
        if (this.title == EMPTY && this.description == EMPTY && this.author == EMPTY)
            return true;
        return false;
    }

    public boolean containsTxt(String msg) {
        if (this.title.toLowerCase().contains(msg) ||
                this.description.toLowerCase().contains(msg) || this.author.toLowerCase().contains(msg))
            return true;
        return false;
    }

}
