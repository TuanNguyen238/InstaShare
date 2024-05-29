package com.example.instashare.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String uri;

    public User(){}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(uri);
    }

    protected User(Parcel in) {
        uid = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        password = in.readString();
        uri = in.readString();
    }

    public User(String uid, String firstName, String lastName, String email, String password, String uri) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.uri = uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName()
    {
        return this.firstName + " " + this.lastName;
    }
}
