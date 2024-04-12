package com.PrathihasProjects.PrathihasSplitwise.entity;

import jakarta.persistence.*;

import java.util.Date;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private int id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Groups group;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "payer_username", referencedColumnName = "username")
    private User payer;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "payee_username", referencedColumnName = "username")
    private User payee;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "payment_date")
    private Date paymentDate;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    public Payments() {
    }

    public Payments(Groups group, User payee, User payer, BigDecimal amount, Date paymentDate)
    {
        this.group = group;
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Groups getGroup() {
        return group;
    }

    public void setGroup(Groups group) {
        this.group = group;
    }

    public User getPayer() {
        return payer;
    }

    public void setPayer(User payer) {
        this.payer = payer;
    }

    public User getPayee() {
        return payee;
    }

    public void setPayee(User payee) {
        this.payee = payee;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Payments{" +
                "id=" + id +
                ", group=" + group +
                ", payer=" + payer +
                ", payee=" + payee +
                ", deleted=" + deleted +
                ", paymentDate=" + paymentDate +
                ", amount=" + amount +
                '}';
    }

}
