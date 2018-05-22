package org.androidtown.assignment;

/**
 * Created by Bin on 2018-05-18.
 */

public class Member_list {
    public String ID;
    public String mapped_trainer;


    public Member_list() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Member_list(String text) {
        this.mapped_trainer = mapped_trainer;
    }

    public String getUser_id() {
        return ID;
    }
    public void setUser_id(String ID) {
        this.ID = ID;
    }
    public String getText() {
        return mapped_trainer;
    }

    public void setText(String text) {
        this.mapped_trainer = mapped_trainer;
    }
}
