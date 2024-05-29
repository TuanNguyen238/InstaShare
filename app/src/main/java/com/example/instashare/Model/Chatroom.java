package com.example.instashare.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;

public class Chatroom implements Parcelable {
    String idchatroom;
    Timestamp lastMessageTimestamp;
    String lastMessageSenderId;
    String lastMessage;
    String uid1;
    String uid2;
    String state;
    String state1;
    String state2;

    public String getState1() {
        return state1;
    }

    public void setState1(String state1) {
        this.state1 = state1;
    }

    public String getState2() {
        return state2;
    }

    public void setState2(String state2) {
        this.state2 = state2;
    }

    public Chatroom(DataSnapshot ds){
        long seconds= ds.child("lastMessageTimestamp/seconds").getValue(Long.class);
        int nanoseconds= ds.child("lastMessageTimestamp/nanoseconds").getValue(Integer.class);
        this.lastMessageTimestamp = new Timestamp(seconds, nanoseconds);
        this.idchatroom = ds.child("idchatroom").getValue(String.class);
        this.lastMessage= ds.child("lastMessage").getValue(String.class);
        this.state1 = ds.child("state1").getValue(String.class);
        this.state2 = ds.child("state2").getValue(String.class);
        this.lastMessageSenderId = ds.child("lastMessageSenderId").getValue(String.class);
        this.uid1 = ds.child("uid1").getValue(String.class);
        this.uid2 = ds.child("uid2").getValue(String.class);
    }

    protected Chatroom(Parcel in) {
        idchatroom = in.readString();
        lastMessageTimestamp = in.readParcelable(Timestamp.class.getClassLoader());
        lastMessageSenderId = in.readString();
        lastMessage = in.readString();
        uid1 = in.readString();
        uid2 = in.readString();
        state1 = in.readString();
        state2 = in.readString();
    }

    public static final Creator<Chatroom> CREATOR = new Creator<Chatroom>() {
        @Override
        public Chatroom createFromParcel(Parcel in) {
            return new Chatroom(in);
        }

        @Override
        public Chatroom[] newArray(int size) {
            return new Chatroom[size];
        }
    };

    public String getUid1() {
        return uid1;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }
    public String getUid2() {
        return uid2;
    }

    public Chatroom(String idchatroom, Timestamp lastMessageTimestamp, String lastMessageSenderId, String lastMessage, String uid1, String uid2, String state1, String state2) {
        this.idchatroom = idchatroom;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessage = lastMessage;
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.state1 = state1;
        this.state2 = state2;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    public Chatroom(String idchatroom, Timestamp lastMessageTimestamp, String lastMessageSenderId, String lastMessage, String uid1, String uid2, String state) {
        this.idchatroom = idchatroom;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessage = lastMessage;
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.state = state;
    }

    public Chatroom() {
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIdchatroom() {
        return idchatroom;
    }

    public void setIdchatroom(String idchatroom) {
        this.idchatroom = idchatroom;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(idchatroom);
        dest.writeString(lastMessage);
        dest.writeString(lastMessageSenderId);
        dest.writeValue(lastMessageTimestamp);
        dest.writeString(uid1);
        dest.writeString(uid2);
        dest.writeString(state1);
        dest.writeString(state2);
    }
}
