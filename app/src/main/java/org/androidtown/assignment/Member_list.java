package org.androidtown.assignment;

/**
 * Created by Bin on 2018-05-18.
 */

public class Member_list {
    public String[] memberlist;

    public Member_list() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Member_list(String[] memberlist) {
        this.memberlist = memberlist;
    }
    public String[] getMemberlist() {
        return memberlist;
    }
    public void setMemberlist(String[] memberlist) {
        this.memberlist = memberlist;
    }


}
