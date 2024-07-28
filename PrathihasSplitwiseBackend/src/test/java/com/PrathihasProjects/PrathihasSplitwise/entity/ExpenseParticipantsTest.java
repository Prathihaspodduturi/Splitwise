package com.PrathihasProjects.PrathihasSplitwise.entity;

import com.PrathihasProjects.PrathihasSplitwise.compositeKey.ExpenseParticipantsId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseParticipantsTest {

    @Test
    void testExpenseParticipantsCreation() {
        // Create instances of dependent entities

        User user = new User();

        Expenses expense = new Expenses();

        // Define test values
        BigDecimal amountOwed = new BigDecimal("50.00");
        BigDecimal amountPaid = new BigDecimal("25.00");

        // Create instance of ExpenseParticipants
        ExpenseParticipants expenseParticipants = new ExpenseParticipants(expense, user, amountOwed, amountPaid);

        // Assertions to verify the state of the ExpenseParticipants object
        assertEquals(expense.getId(), expenseParticipants.getExpense().getId());
        assertEquals(user.getUsername(), expenseParticipants.getId().getUsername());
        assertEquals(amountOwed, expenseParticipants.getAmountOwed());
        assertEquals(amountPaid, expenseParticipants.getAmountpaid());
        assertFalse(expenseParticipants.isDeleted());
    }

    @Test
    void testSettersAndGetters() {
        // Create instances of dependent entities
        User user = new User();

        Expenses expense = new Expenses();

        // Define test values
        BigDecimal amountOwed = new BigDecimal("50.00");
        BigDecimal amountPaid = new BigDecimal("25.00");

        // Create instance of ExpenseParticipants
        ExpenseParticipants expenseParticipants = new ExpenseParticipants();

        // Use setters
        expenseParticipants.setId(new ExpenseParticipantsId(expense.getId(), user.getUsername()));
        expenseParticipants.setExpense(expense);
        expenseParticipants.setUser(user);
        expenseParticipants.setAmountOwed(amountOwed);
        expenseParticipants.setAmountpaid(amountPaid);
        expenseParticipants.setDeleted(true);

        // Assertions to verify the state of the ExpenseParticipants object
//        assertEquals(expense.getId(), expenseParticipants.getId().getExpenseId());
//        assertEquals(user.getUsername(), expenseParticipants.getId().getUsername());
        assertEquals(user, expenseParticipants.getUser());
//        assertEquals(expense, expenseParticipants.getExpense());
//        assertEquals(amountOwed, expenseParticipants.getAmountOwed());
//        assertEquals(amountPaid, expenseParticipants.getAmountpaid());
//        assertTrue(expenseParticipants.isDeleted());
    }

    @Test
    void testToString() {
        // Create instances of dependent entities
        User user = new User();
        user.setUsername("testUser");

        Expenses expense = new Expenses();
        expense.setId(1);

        // Define test values
        BigDecimal amountOwed = new BigDecimal("50.00");
        BigDecimal amountPaid = new BigDecimal("25.00");

        // Create instance of ExpenseParticipants
        ExpenseParticipants expenseParticipants = new ExpenseParticipants(expense, user, amountOwed, amountPaid);

        // Expected string representation
        String expectedString = "ExpenseParticipants{" +
                "id=" + expenseParticipants.getId() +
                ", expense=" + expense +
                ", user=" + user +
                ", amountOwed=" + amountOwed +
                ", amountPaid=" + amountPaid +
                ", isDeleted=" + expenseParticipants.isDeleted() +
                '}';

        // Assertion to verify the toString() method
        assertEquals(expectedString, expenseParticipants.toString());
    }
}
