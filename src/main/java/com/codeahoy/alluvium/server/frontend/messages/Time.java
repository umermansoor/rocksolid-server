package com.codeahoy.alluvium.server.frontend.messages;

import java.util.Date;

/**
 * @author umansoor
 */
public class Time implements Message{
    public Date date;

    public Time() {
        date = new Date();
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String toString() {
        return "The server time is: " + date;
    }

}
