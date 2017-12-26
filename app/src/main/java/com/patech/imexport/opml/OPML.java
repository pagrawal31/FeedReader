package com.patech.imexport.opml;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Mohit on 25/11/17.
 */

@Getter
@Setter
public class OPML {
    private String title;
    private Date created;
    private Date modified;
    private String ownerName;
    private String ownerEmail;
    private String docs;

    private List<Outline> outlines;
}
