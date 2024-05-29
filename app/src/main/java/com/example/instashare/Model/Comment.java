package com.example.instashare.Model;

import android.net.Uri;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Comment {
    String state;
    Timestamp timestamp;
    String emoji;
    Uri uri;
    String sendId;

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public Comment(QueryDocumentSnapshot doc ){
        this.state = doc.getString("state");
        this.emoji = doc.getString("emoji");
        this.sendId = doc.getString("sendId");
        this.timestamp = doc.getTimestamp("timestamp");
        try{
            if(doc.get("uri").toString().isEmpty())
                this.uri = null;
            else
                this.uri = Uri.parse(doc.getString("uri"));
        } catch (Exception e){
            this.uri = null ;
        }
    }

    public Comment(String state, Timestamp timestamp, String emoji, Uri uri, String sendId) {
        this.state = state;
        this.timestamp = timestamp;
        this.emoji = emoji;
        this.uri = uri;
        this.sendId = sendId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "state='" + state + '\'' +
                ", timestamp=" + timestamp +
                ", emoji='" + emoji + '\'' +
                ", uri=" + uri +
                ", sendId='" + sendId + '\'' +
                '}';
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }


    public Comment() {
    }
}
