package com.PrathihasProjects.PrathihasSplitwise.dto;

import java.util.Date;

public class MemberInfo {
    private String username;
    private String removedBy;
    private Date removedDate;

    public MemberInfo(String username, String removedBy, Date removedDate) {
        this.username = username;
        this.removedBy = removedBy;
        this.removedDate = removedDate;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRemovedBy() {
        return removedBy;
    }

    public void setRemovedBy(String removedBy) {
        this.removedBy = removedBy;
    }

    public Date getRemovedDate() {
        return removedDate;
    }

    public void setRemovedDate(Date removedDate) {
        this.removedDate = removedDate;
    }
}

