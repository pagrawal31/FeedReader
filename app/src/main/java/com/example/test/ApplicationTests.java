package com.example.test;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.patech.imexport.opml.OpmlParser;
import com.java.rssfeed.model.feed.Outline;
import com.patech.utils.AppUtils;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ApplicationTests {

    private String resourceDir = "resources";
    private final String SEPARATOR = "/";

    @Test
	public void addition_isCorrect() throws Exception {
		assertEquals(4, 2 + 2);
	}

    @Test
    public void testOpmlImport() throws Exception {
        String dirPath = getResourceDir();
        String fileName = "example.opml";
        File f = new File(dirPath + SEPARATOR + fileName);
        String path;
        if (!f.exists()) {
            System.out.println(f.getAbsoluteFile());
            path = f.getAbsolutePath();
        }
        List<Outline> outlines = OpmlParser.read(f);
        assert (outlines.size() == 68);

        File outputFile = new File(dirPath + SEPARATOR + "output_example.opml");
        OpmlParser.write(outlines, outputFile);

    }
    @Test
    public void testRssDateFormat() {
        String pdtDate = "Fri, 27 Jul 2018 07:47:00 PDT";
        pdtDate = "Fri, 27 Jul 2018 08:13:00 PDT";
        Date date;
        String pubDate = "EMPTY";
        try {
            date = AppUtils.RSS_DATE_FORMATTER.parse(pdtDate);
            pubDate = AppUtils.STANDARD_DATE_FORMATTER.format(date);
        } catch (ParseException pe) {
            date = null;
        }
        System.out.println(pubDate);
    }
    @Test
    public void testDateComparator() {
        String sDates[] ={
                null,
                "31/12/1998 23:37:50",
                "31/12/1998 23:37:50",
                null,
                "31/12/1998 22:37:50",
                "31/12/1998 22:38:50",
                "31/12/1998 23:38:50",
        };
        Date date[] = new Date[sDates.length];

        Date date1 = null;

        int counter = 0;
        for (String sdate : sDates) {
            try {
                date[counter] = AppUtils.STANDARD_DATE_FORMATTER.parse(sdate);
            } catch (Exception e) {
                date[counter] = null;
            }
            counter++;
        }

        List<Date> dateList = Arrays.asList(date);
        Collections.sort(dateList, new AppUtils.DateComparator());
        for (Date currDate : dateList) {
            System.out.println(currDate);
        }
    }


    public String getResourceDir() {
        return resourceDir;
    }
}


