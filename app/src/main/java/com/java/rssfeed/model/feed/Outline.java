package com.java.rssfeed.model.feed;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Mohit on 25/11/17.
 */

@Getter
@Setter
public class Outline {
    //required
    private String text;
    private Date created;
    private String category;

    private List<Feed> subscriptions;
}
