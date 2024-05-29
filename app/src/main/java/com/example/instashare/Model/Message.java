package com.example.instashare.Model;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Message {
    private String message;
    private String senderId;
    private Timestamp timestamp;
    private Uri uri;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
    public void setUriNull(){
        this.uri = Uri.parse("null");
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", senderId='" + senderId + '\'' +
                ", timestamp=" + timestamp +
                ", uri=" + uri +
                '}';
    }
    public Message(String message, String senderId, Timestamp timestamp, Uri uri) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.uri = uri;
    }


    public Message(QueryDocumentSnapshot doc ){
        this.message = doc.getString("message");
        this.senderId = doc.getString("senderId");
        this.timestamp = doc.getTimestamp("timestamp");
        Log.i("search_1", "Message: " + this.message);
        try{
            if(doc.get("uri").toString().isEmpty())
                this.uri = null;
            else
                this.uri = Uri.parse(doc.getString("uri"));
        } catch (Exception e){
            this.uri = null ;
        }

    }
    public Message() {
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
