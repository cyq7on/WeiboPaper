package com.xihua.weibopaper.bean;

import java.util.List;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.bean
 * @Description:微博内容实体类
 * @date 2015/12/2511:18
 */
public class WeiboContent {


    private boolean hasvisible;
    private int previous_cursor;
    private String next_cursor;
    private int total_number;
    private int interval;
    private List<StatusContent> statuses;



    public void setHasvisible(boolean hasvisible) {
        this.hasvisible = hasvisible;
    }

    public void setPrevious_cursor(int previous_cursor) {
        this.previous_cursor = previous_cursor;
    }

    public void setNext_cursor(String next_cursor) {
        this.next_cursor = next_cursor;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isHasvisible() {
        return hasvisible;
    }

    public int getPrevious_cursor() {
        return previous_cursor;
    }

    public String getNext_cursor() {
        return next_cursor;
    }

    public int getTotal_number() {
        return total_number;
    }

    public int getInterval() {
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
