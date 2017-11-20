package com.java.rssfeed.utils;

import java.util.Date;

public class DateUtils {

    public static void main(String[] args){
        Date date1 = new Date("20/06/2017 16:36:26");
        Date date2 = new Date("20/06/2017 16:40:10");
        Date date3 = new Date("20/06/2017 15:40:10");
        
        if (date2.after(date1)) {
            System.out.println(date2 + " is greater than " + date1);
        } else {
            System.out.println(date2 + " is lesser than " + date1);
        }
        
        if (date3.after(date1)) {
            System.out.println(date3 + " is greater than " + date1);
        } else {
            System.out.println(date3 + " is lesser than " + date1);
        }
        
    }
}
