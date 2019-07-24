package com.example.michael.firstapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class User implements Parcelable {

    private String email;
    private String username;

    /**
     * Complete constructor
     *
     * @param email    user email
     * @param username user password
     */
    public User(String email, String username) {
        this.email = email;
        this.username = username;
    }

    /**
     * Empty constructor
     */
    public User() {

    }

    /**
     * Used to get values from input parcel
     *
     * @param in input Parcel containing user data
     */
    protected User(Parcel in) {
        email = in.readString();
        username = in.readString();
    }

    /**
     * Creates uesr from parcel and creates array of data
     */
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    /**
     * @return user email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email user email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username user's email
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return all values of marker as single String
     */
    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes values of user to parcel
     *
     * @param dest  dest
     * @param flags flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(username);
    }
}