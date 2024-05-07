package com.PrathihasProjects.PrathihasSplitwise.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "`groups`")
public class Groups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "description")
    private String groupDescription;

    @Column(name = "date_created")
    private Date dateCreated;

    @Column(name = "settled_date")
    private Date settledDate;

    @Column(name = "deleted_date")
    private Date deletedDate;

    @Column(name = "settled_up", nullable = false)
    private boolean settledUp = false;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "created_by", referencedColumnName = "username")
    private User createdBy;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "deleted_by", referencedColumnName = "username")
    private User deletedBy;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "settled_by", referencedColumnName = "username")
    private User settledBy;

   /* @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "username")
    )
    private Set<User> users;

    // Getters and setters...

    public Set<User> getMembers() {
        return users;
    }

    public void setMembers(Set<User> users) {
        this.users = users;
    } */

    public Groups() {
    }

    public Groups(String groupName, String groupDescription, Date dateCreated, User createdBy) {
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.dateCreated = dateCreated;
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isSettledUp() {
        return settledUp;
    }

    public void setSettledUp(boolean settledUp) {
        this.settledUp = settledUp;
    }

    public Date getSettledDate() {
        return settledDate;
    }

    public void setSettledDate(Date settledDate) {
        this.settledDate = settledDate;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public User getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(User deletedBy) {
        this.deletedBy = deletedBy;
    }

    public User getSettledBy() {
        return settledBy;
    }

    public void setSettledBy(User settledBy) {
        this.settledBy = settledBy;
    }

    @Override
    public String toString() {
        return "Groups{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", groupDescription='" + groupDescription + '\'' +
                ", dateCreated=" + dateCreated +
                ", settledDate=" + settledDate +
                ", deletedDate=" + deletedDate +
                ", settledUp=" + settledUp +
                ", createdBy=" + createdBy +
                ", deleted=" + deleted +
                ", deletedBy=" + deletedBy +
                ", settledBy=" + settledBy +
                '}';
    }
}
