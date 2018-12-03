package com.major.touristguide;

public class EditItem {
    private String placeId;
    private String title;
    private String desc;
    private boolean selected;

    public EditItem(String id, String title, String desc) {
        this.placeId = id;
        this.title = title;
        this.desc = desc;
        this.selected = true;
    }
    public String getId() {
        return placeId;
    }
    public void setId(String id) {
        this.placeId = id;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    @Override
    public String toString() {
        return title + "\n" + desc;
    }
}
