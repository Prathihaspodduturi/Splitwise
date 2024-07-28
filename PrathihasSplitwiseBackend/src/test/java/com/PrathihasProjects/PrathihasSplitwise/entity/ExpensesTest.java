package com.PrathihasProjects.PrathihasSplitwise.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ExpensesTest {

    private Expenses expense;
    @BeforeEach
    public void setUp()
    {
        expense = new Expenses();
    }

   @Test
   void testExpenses() {
       User addedByUser = new User();
       addedByUser.setUsername("testUser");

       Groups group = new Groups();
       group.setId(1);

       // Define test values
       BigDecimal amount = new BigDecimal("100.00");
       String expenseName = "Test Expense";
       Date dateCreated = new Date();

       // Create instance of Expenses
       expense = new Expenses(group, amount, expenseName, dateCreated, addedByUser);

       // Assertions to verify the state of the Expenses object
       assertEquals(group, expense.getGroupId());
       assertEquals(amount, expense.getAmount());
       assertEquals(expenseName, expense.getExpenseName());
       assertEquals(dateCreated, expense.getDateCreated());
       assertEquals(addedByUser, expense.getAddedBy());
       assertFalse(expense.isDeleted());
       assertFalse(expense.isPayment());
       assertNull(expense.getDeletedBy());
       assertNull(expense.getDeletedDate());
       assertNull(expense.getLastUpdatedDate());
       assertNull(expense.getUpdatedBy());
   }

    @Test
    public void testSetAndGetId() {
        expense.setId(2);
        assertEquals(2, expense.getId());
    }

    @Test
    public void testSetAndGetAmount() {
        expense.setAmount(new BigDecimal("200.00"));
        assertEquals(new BigDecimal("200.00"), expense.getAmount());
    }

    @Test
    public void testSetAndGetExpenseName() {
        expense.setExpenseName("Dinner");
        assertEquals("Dinner", expense.getExpenseName());
    }

    @Test
    public void testSetAndDateCreated() {
        Date newDate = new Date();
        expense.setDateCreated(newDate);
        assertEquals(newDate, expense.getDateCreated());
    }


    @Test
    public void testSetAndGetDeleted() {
        expense.setDeleted(true);
        assertTrue(expense.isDeleted());
    }

    @Test
    public void testSetAndGetPayment() {
        expense.setPayment(false);
        assertFalse(expense.isPayment());
    }

    @Test
    public void testSetAndGetGroupId() {
        Groups newGroup = new Groups();
        expense.setGroupId(newGroup);
        assertEquals(newGroup, expense.getGroupId());
    }

    @Test
    public void testSetAndGetAddedBy() {
        User newUser = new User();
        expense.setAddedBy(newUser);
        assertEquals(newUser, expense.getAddedBy());
    }

    @Test
    public void testSetAndGetLastUpdatedDate() {
        Date newDate = new Date();
        expense.setLastUpdatedDate(newDate);
        assertEquals(newDate, expense.getLastUpdatedDate());
    }

    @Test
    public void testSetAndGetUpdatedBy() {
        User newUser = new User();
        expense.setUpdatedBy(newUser);
        assertEquals(newUser, expense.getUpdatedBy());
    }


    @Test
    public void testSetAndGetDeletedBy() {
        User newUser = new User();
        expense.setDeletedBy(newUser);
        assertEquals(newUser, expense.getDeletedBy());
    }

    @Test
    public void testSetAndGetDeletedDate() {
        Date newDate = new Date();
        expense.setDeletedDate(newDate);
        assertEquals(newDate, expense.getDeletedDate());
    }

    @Test
    void testToString() {
        // Create actual instances of dependencies
        User addedByUser = new User();
        addedByUser.setUsername("testUser");

        Groups group = new Groups();
        group.setId(1);

        // Define test values
        BigDecimal amount = new BigDecimal("100.00");
        String expenseName = "Test Expense";
        Date dateCreated = new Date();

        // Create instance of Expenses
        Expenses expenses = new Expenses(group, amount, expenseName, dateCreated, addedByUser);

        // Manually construct the expected string
        String expectedString = "Expenses{" +
                "id=0" +  // Default ID value
                ", amount=" + amount +
                ", expenseName='" + expenseName + '\'' +
                ", dateCreated=" + dateCreated +
                ", deleted=false" +
                ", isPayment=false" +
                ", deletedBy=null" +
                ", deletedDate=null" +
                ", groupId=" + group +
                ", addedBy=" + addedByUser +
                ", lastUpdatedDate=null" +
                ", updatedBy=null" +
                '}';

        // Assertion to verify the toString method
        assertEquals(expectedString, expenses.toString());
    }

}
