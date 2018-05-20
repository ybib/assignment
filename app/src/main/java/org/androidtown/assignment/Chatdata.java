package org.androidtown.assignment;

/**
 * Created by Bin on 2018-05-18.
 */

public class Chatdata {
    public String ID;
    public String text;

    public Chatdata() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Chatdata(String text) {
        this.text = text;
    }

    public String getUser_id() {
        return ID;
    }
    public void setUser_id(String ID) {
        this.ID = ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
