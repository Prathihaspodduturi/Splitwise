package com.PrathihasProjects.PrathihasSplitwise.compositeKey;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ExpenseParticipantsId implements Serializable {
    private int expenseId; // Assuming expense_id is of type int
    private String username;

    public ExpenseParticipantsId() {
    }

    public ExpenseParticipantsId(int expenseId, String username) {
        this.expenseId = expenseId;
        this.username = username;
    }

    // Getters and setters
    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Implement equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpenseParticipantsId that = (ExpenseParticipantsId) o;

        if (expenseId != that.expenseId) return false;
        return username != null ? username.equals(that.username) : that.username == null;
    }

    @Override
    public int hashCode() {
        int result = expenseId;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }
}

