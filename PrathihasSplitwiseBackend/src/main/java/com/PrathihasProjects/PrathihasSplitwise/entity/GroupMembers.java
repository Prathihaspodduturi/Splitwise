package com.PrathihasProjects.PrathihasSplitwise.entity;

import com.PrathihasProjects.PrathihasSplitwise.compositeKey.GroupMembersId;
import jakarta.persistence.*;

import java.util.Date;

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


    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "removed_by", referencedColumnName = "username", nullable = true)
    private User removedBy;

    @Column(name = "removed_date", nullable = true)
    private Date removedDate;

    @Column(name = "added_date")
    private Date addedDate;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "added_by", referencedColumnName = "username", nullable = true)
    private User addedBy;

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


    public User getRemovedBy() {
        return removedBy;
    }

    public void setRemovedBy(User removedBy) {
        this.removedBy = removedBy;
    }

    public Date getRemovedDate() {
        return removedDate;
    }

    public void setRemovedDate(Date removedDate) {
        this.removedDate = removedDate;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public User getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(User addedBy) {
        this.addedBy = addedBy;
    }

    @Override
    public String toString() {
        return "GroupMembers{" +
                "id=" + id +
                ", group=" + group +
                ", user=" + user +
                ", removedBy=" + removedBy +
                ", removedDate=" + removedDate +
                ", addedDate=" + addedDate +
                ", addedBy=" + addedBy +
                '}';
    }

}

