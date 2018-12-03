package com.major.touristguide;

public class ItemObject {
    private String contents;
    private String id;
    private String days;
    private String startDate;
    public ItemObject(String contents, String id, String days, String startDate) {
        this.contents = contents;
        this.id = id;
        this.days = days;
        this.startDate = startDate;
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

    public String getStartDate() { return startDate;}
}
