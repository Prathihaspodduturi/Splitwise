package com.PrathihasProjects.PrathihasSplitwise.entity;

import com.PrathihasProjects.PrathihasSplitwise.compositeKey.ExpenseParticipantsId;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "expenseparticipants")
public class ExpenseParticipants {

    @EmbeddedId
    private ExpenseParticipantsId id = new ExpenseParticipantsId();

    @ManyToOne
    @MapsId("expenseId")
    @JoinColumn(name = "expense_id", referencedColumnName = "expense_id")
    private Expenses expense;

    @ManyToOne
    @MapsId("username")
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @Column(name = "amount_owed", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountOwed;


    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountpaid;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    // Default constructor
    public ExpenseParticipants() {}

    // Full constructor
    public ExpenseParticipants(Expenses expense, User user, BigDecimal amountOwed, BigDecimal amountPaid) {
        this.expense = expense;
        this.user = user;
        this.amountOwed = amountOwed;
        this.amountpaid = amountPaid;
        // Update the embedded ID
        this.id.setExpenseId(expense.getId()); // Make sure the Expenses entity has getId()
        this.id.setUsername(user.getUsername()); // Make sure the User entity has getUsername()
    }

    // Getters and Setters
    public ExpenseParticipantsId getId() {
        return id;
    }

    public void setId(ExpenseParticipantsId id) {
        this.id = id;
    }

    public Expenses getExpense() {
        return expense;
    }

    public void setExpense(Expenses expense) {
        this.expense = expense;
        this.id.setExpenseId(expense.getId()); // Synchronize the ID when setting the expense
    }

    public BigDecimal getAmountpaid() {
        return amountpaid;
    }

    public void setAmountpaid(BigDecimal amountpaid) {
        this.amountpaid = amountpaid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.id.setUsername(user.getUsername()); // Synchronize the ID when setting the user
    }

    public BigDecimal getAmountOwed() {
        return amountOwed;
    }

    public void setAmountOwed(BigDecimal amountOwed) {
        this.amountOwed = amountOwed;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }


    // Implement toString() if needed
    @Override
    public String toString() {
        return "ExpenseParticipants{" +
                "id=" + id +
                ", expense=" + expense +
                ", user=" + user +
                ", amountOwed=" + amountOwed +
                ", amountPaid=" + amountpaid +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
