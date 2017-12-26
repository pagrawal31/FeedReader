package com.patech.imexport.opml;

import java.net.URI;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Mohit on 25/11/17.
 */

@Getter
@Setter
public class Subscription {
    //required
    private String type;
    private String text;
    private String xmlUrl;

    private String description;
    private String htmlUrl;
    private String language;
    private String title;
    private String version; // we only support 2.0
}
