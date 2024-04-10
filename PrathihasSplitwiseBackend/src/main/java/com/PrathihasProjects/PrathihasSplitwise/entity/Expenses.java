package com.PrathihasProjects.PrathihasSplitwise.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "expenses")
public class Expenses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "amount")
    private float amount;

    @Column(name = "decription")
    private String description;

    @Column(name = "date_created")
    private Date dateCreated;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "paid_by", referencedColumnName = "username")
    private User paidBy;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private groups groupId;

    public Expenses(){}

    public Expenses(groups groupId, User paidBy, Float amount,String description, Date dateCreated){
        this.groupId = groupId;
        this.paidBy = paidBy;
        this.amount = amount;
        this.description = description;
        this.dateCreated = dateCreated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(User paidBy) {
        this.paidBy = paidBy;
    }

    public groups getGroupId() {
        return groupId;
    }

    public void setGroupId(groups groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "Expenses{" +
                "id=" + id +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", dateCreated=" + dateCreated +
                ", paidBy=" + paidBy +
                ", groupId=" + groupId +
                '}';
    }
}
