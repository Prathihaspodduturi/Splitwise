package com.PrathihasProjects.PrathihasSplitwise.helper;

import java.util.Date;

public class GroupMembersHelper {

    private String username;
    private int groupId;

    private String removedBy = null;

    private String addedBy;

    private Date addedDate;

    private Date removedDate = null;
    public GroupMembersHelper(){
    }

    public GroupMembersHelper(String username, int groupId, String addedBy, Date addedDate) {
        this.username = username;
        this.groupId = groupId;
        this.addedBy = addedBy;
        this.addedDate = addedDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getRemovedBy() {
        return removedBy;
    }

    public void setRemovedBy(String removedBy) {
        this.removedBy = removedBy;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public Date getRemovedDate() {
        return removedDate;
    }

    public void setRemovedDate(Date removedDate) {
        this.removedDate = removedDate;
    }

    @Override
    public String toString() {
        return "GroupMembersHelper{" +
                "username='" + username + '\'' +
                ", groupId=" + groupId +
                ", removedBy='" + removedBy + '\'' +
                ", addedBy='" + addedBy + '\'' +
                ", addedDate=" + addedDate +
                ", removedDate=" + removedDate +
                '}';
    }
}
