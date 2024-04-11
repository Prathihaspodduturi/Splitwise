package com.PrathihasProjects.PrathihasSplitwise.compositeKey;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class GroupMembersId implements Serializable {
    private int groupId;  // corresponds to the primary key of Groups
    private String username;  // corresponds to the primary key of User

    public GroupMembersId() {}

    public GroupMembersId(int groupId, String username) {
        this.groupId = groupId;
        this.username = username;
    }

    // Getters and setters
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Implement equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupMembersId that = (GroupMembersId) o;

        if (groupId != that.groupId) return false;
        return username != null ? username.equals(that.username) : that.username == null;
    }

    @Override
    public int hashCode() {
        int result = groupId;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }
}

