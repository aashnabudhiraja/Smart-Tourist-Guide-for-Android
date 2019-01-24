package com.major.touristguide.models;

public class RowItem {
    private String timing;
    private String title;
    private String desc;

    public RowItem(String timing, String title, String desc) {
        this.timing = timing;
        this.title = title;
        this.desc = desc;
    }
    public String getTiming() {
        return timing;
    }
    public void setTiming(String timing) {
        this.timing = timing;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title + "\n" + desc;
    }
}
