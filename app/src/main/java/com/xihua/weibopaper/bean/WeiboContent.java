package com.xihua.weibopaper.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.bean
 * @Description:微博内容实体类
 * @date 2015/12/2511:18
 */
public class WeiboContent implements Serializable{


    private boolean hasvisible;
    private long previous_cursor;
    private String next_cursor;
    private long total_number;
    private long interval;
    private List<StatusContent> statuses;



    public void setHasvisible(boolean hasvisible) {
        this.hasvisible = hasvisible;
    }

    public void setPrevious_cursor(long previous_cursor) {
        this.previous_cursor = previous_cursor;
    }

    public void setNext_cursor(String next_cursor) {
        this.next_cursor = next_cursor;
    }

    public void setTotal_number(long total_number) {
        this.total_number = total_number;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public boolean isHasvisible() {
        return hasvisible;
    }

    public long getPrevious_cursor() {
        return previous_cursor;
    }

    public String getNext_cursor() {
        return next_cursor;
    }

    public long getTotal_number() {
        return total_number;
    }

    public long getInterval() {
        return interval;
    }

    public List<StatusContent> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<StatusContent> statuses) {
        this.statuses = statuses;
    }

    @Override
    public String toString() {
        return "WeiboContent{" +
                "hasvisible=" + hasvisible +
                ", previous_cursor=" + previous_cursor +
                ", next_cursor=" + next_cursor +
                ", total_number=" + total_number +
                ", interval=" + interval +
                ", statuses=" + statuses +
                '}';
    }
}
