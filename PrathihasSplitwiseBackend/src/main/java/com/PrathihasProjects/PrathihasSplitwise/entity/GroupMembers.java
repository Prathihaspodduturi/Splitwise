package com.PrathihasProjects.PrathihasSplitwise.entity;

import com.PrathihasProjects.PrathihasSplitwise.compositeKey.GroupMembersId;
import jakarta.persistence.*;

@Entity
@Table(name = "group_members")
public class GroupMembers {

    @EmbeddedId
    private GroupMembersId id;  // Embedded ID instance

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @MapsId("groupId")  // Maps the groupId part of the composite ID
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Groups group;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @MapsId("username")  // Maps the username part of the composite ID
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    // Constructors
    public GroupMembers() {}

    public GroupMembers(Groups group, User user) {
        this.group = group;
        this.user = user;
        this.id = new GroupMembersId(group.getId(), user.getUsername());
    }

    // Getters and setters
    public GroupMembersId getId() {
        return id;
    }

    public void setId(GroupMembersId id) {
        this.id = id;
    }

    public Groups getGroup() {
        return group;
    }

    public void setGroup(Groups group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

