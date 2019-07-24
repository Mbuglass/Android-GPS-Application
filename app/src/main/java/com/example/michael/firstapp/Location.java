package com.example.michael.firstapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

public class Location implements Parcelable {

    private String placedBy;
    private GeoPoint coordinates;
    private String locationName;
    private String category;
    private String description;
    private Integer score;

    /**
     * Complete constructor
     *
     * @param placedBy     username of the user who placed the marker
     * @param coordinates  longitude and latitude of the marker
     * @param locationName the chosen name for the marker
     * @param category     the category the marker falls under
     * @param description  the description of the marker
     */
    public Location(String placedBy, GeoPoint coordinates, String locationName, String category,
                    String description) {
        this.placedBy = placedBy;
        this.coordinates = coordinates;
        this.locationName = locationName;
        this.category = category;
        this.description = description;
    }

    /**
     * Empty constructor
     */
    public Location() {
    }

    /**
     * Used to get values from input parcel
     *
     * @param in input Parcel containing marker data
     */
    protected Location(Parcel in) {
        Double lat = in.readDouble();
        Double lng = in.readDouble();
        coordinates = new GeoPoint(lat, lng);
        locationName = in.readString();
        category = in.readString();
        description = in.readString();
        placedBy = in.readString();
        score = in.readInt();
    }

    /**
     * Creates location from parcel and creates array of data
     */
    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    /**
     * @return username of the user who placed the marker
     */
    public String getPlacedBy() {
        return placedBy;
    }

    /**
     * @param placedBy contains username of user who placed the marker
     */
    public void setPlacedBy(String placedBy) {
        this.placedBy = placedBy;
    }

    /**
     * @return coordinates of the marker as GeoPoint object
     */
    public GeoPoint getCoordinates() {
        return coordinates;
    }

    /**
     * @param coordinates contains coordinates of marker
     */
    public void setCoordinates(GeoPoint coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * @return string containing location name
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * @param locationName string containing location name
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * @return marker category as String
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category category as String
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return marker description as String
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description Marker description as String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return score of marker
     */
    public Integer getScore() {
        return score;
    }

    /**
     * @param score score of marker
     */
    public void setScore(Integer score) {
        this.score = score;
    }

    /**
     * @return all values of marker as single String
     */
    @NonNull
    @Override
    public String toString() {
        return "Location{" +
                "coordinates=" + coordinates +
                ", locationName=" + locationName +
                ", category=" + category +
                ", description=" + description +
                ", placedBy=" + placedBy +
                ", score=" + score +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes values of marker to parcel
     *
     * @param dest  dest
     * @param flags flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Lat and Lng have to be split into separate double in lieu of a writeGeoPoint method,
        // which is non-existent
        dest.writeDouble(coordinates.getLatitude());
        dest.writeDouble(coordinates.getLongitude());
        dest.writeString(locationName);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeString(placedBy);
        dest.writeInt(score);
    }
}
