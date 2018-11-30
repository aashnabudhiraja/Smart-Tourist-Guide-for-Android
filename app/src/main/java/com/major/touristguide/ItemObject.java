package com.major.touristguide;

public class ItemObject {
    private String contents;
    private String id;
    private String days;
    public ItemObject(String contents, String id, String days) {
        this.contents = contents;
        this.id = id;
        this.days = days;
    }
    public String getContents() {
        return contents;
    }

    public String getIds() {
        return id;
    }

    public String getDays() {
        return days;
    }
}
