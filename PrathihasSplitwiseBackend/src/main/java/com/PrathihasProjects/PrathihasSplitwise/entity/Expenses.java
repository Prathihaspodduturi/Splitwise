package com.PrathihasProjects.PrathihasSplitwise.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "expenses")
public class Expenses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private int id;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "expensename")
    private String expenseName;

    @Column(name = "date_created")
    private Date dateCreated;

    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "is_payment")
    private boolean isPayment = false;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "deleted_by", referencedColumnName = "username")
    private User deletedBy;

    @Column(name = "deleted_date")
    private Date deletedDate;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Groups groupId;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "added_by", referencedColumnName = "username")
    private User addedBy;

    @Column(name = "last_updated")
    private Date lastUpdatedDate;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "updated_by", referencedColumnName = "username")
    private User updatedBy;

    public Expenses(){}

    public Expenses(Groups groupId, BigDecimal amount, String expenseName, Date dateCreated, User addedBy){
        this.groupId = groupId;
        this.amount = amount;
        this.expenseName = expenseName;
        this.dateCreated = dateCreated;
        this.addedBy = addedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expneseName) {
        this.expenseName = expneseName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Groups getGroupId() {
        return groupId;
    }

    public void setGroupId(Groups groupId) {
        this.groupId = groupId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public User getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(User addedBy) {
        this.addedBy = addedBy;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public User getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(User deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public boolean isPayment() {
        return isPayment;
    }

    public void setPayment(boolean payment) {
        isPayment = payment;
    }

    @Override
    public String toString() {
        return "Expenses{" +
                "id=" + id +
                ", amount=" + amount +
                ", expenseName='" + expenseName + '\'' +
                ", dateCreated=" + dateCreated +
                ", deleted=" + deleted +
                ", isPayment=" + isPayment +
                ", deletedBy=" + deletedBy +
                ", deletedDate=" + deletedDate +
                ", groupId=" + groupId +
                ", addedBy=" + addedBy +
                ", lastUpdatedDate=" + lastUpdatedDate +
                ", updatedBy=" + updatedBy +
                '}';
    }
}
