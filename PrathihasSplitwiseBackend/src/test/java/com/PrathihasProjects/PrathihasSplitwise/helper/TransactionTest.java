package com.PrathihasProjects.PrathihasSplitwise.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {

    private String fromUser;
    private String toUser;
    private BigDecimal amount;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        fromUser ="user1";
        toUser ="user2";
        amount = new BigDecimal("100.00");
        transaction = new Transaction(fromUser, toUser, amount);
    }

    @Test
    void constructorTest() {
        assertEquals(fromUser, transaction.getFromUser());
        assertEquals(toUser, transaction.getToUser());
        assertEquals(amount, transaction.getAmount());
    }

    @Test
    public void testSetAndGetFromUser() {
        String newFromUser = "newUser1";
        transaction.setFromUser(newFromUser);
        assertEquals(newFromUser, transaction.getFromUser());
    }

    @Test
    public void testSetAndGetToUser() {
        String newToUser = "newUser2";
        transaction.setToUser(newToUser);
        assertEquals(newToUser, transaction.getToUser());
    }

    @Test
    public void testSetAndGetAmount() {
        BigDecimal newAmount = new BigDecimal("200.00");
        transaction.setAmount(newAmount);
        assertEquals(newAmount, transaction.getAmount());
    }

    @Test
    public void testToString() {
        String expectedString = "Transaction{" +
                "fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", amount=" + amount +
                '}';
        assertEquals(expectedString, transaction.toString());
    }

}
