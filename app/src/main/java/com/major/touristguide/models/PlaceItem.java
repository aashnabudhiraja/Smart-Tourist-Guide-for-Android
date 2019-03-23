package com.major.touristguide.models;

public class PlaceItem {

    private String placeName;
    private String placeId;
    private String rating;

    public PlaceItem(String placeName, String placeId, String rating) {
        this.placeName = placeName;
        this.placeId = placeId;
        this.rating = rating;
    }
    public String getPlaceName() {
        return placeName;
    }
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
    public String getPlaceId() {
        return placeId;
    }
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    @Override
    public String toString() {
        return placeName + "\n" + rating;
    }
}
