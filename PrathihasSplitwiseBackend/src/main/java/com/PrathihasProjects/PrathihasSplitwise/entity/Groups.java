package com.PrathihasProjects.PrathihasSplitwise.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Groups")
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

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "created_by", referencedColumnName = "username")
    private User createdBy;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

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

    @Override
    public String toString() {
        return "Groups{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", groupDescription='" + groupDescription + '\'' +
                ", dateCreated=" + dateCreated +
                ", createdBy=" + createdBy +
                ", deleted=" + deleted +
                '}';
    }
}
