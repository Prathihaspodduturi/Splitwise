package com.PrathihasProjects.PrathihasSplitwise.compositeKey;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseParticipantsIdTest {

    @Test
    void testEmptyCOnstructor ()
    {
        ExpenseParticipantsId ep = new ExpenseParticipantsId();

        assertNull(ep.getUsername(), "username must be null for empty Constructor");
        assertEquals(0, ep.getExpenseId(), "ExpenseId must be null for empty Constructor");
    }

    @Test
    void testEquals_SameObject () {
        ExpenseParticipantsId ep = new ExpenseParticipantsId(1, "user");
        assertTrue(ep.equals(ep), "Object should be equal to itself");
    }

    @Test
    void testEquals_DifferentObjectWithEqualData () {

        ExpenseParticipantsId ep1 = new ExpenseParticipantsId(1, "user");
        ExpenseParticipantsId ep2 = new ExpenseParticipantsId(1,"user");
        assertTrue(ep1.equals(ep2), "Objects with same data should be equal");
    }

    @Test
    void testEquals_DifferentData () {
        ExpenseParticipantsId ep1 = new ExpenseParticipantsId(1, "user");
        ExpenseParticipantsId ep2 = new ExpenseParticipantsId(1,"user1");
        assertFalse(ep1.equals(ep2), "Objects with different data should not be equal");
    }

    @Test
    void testHashCode_forEqualObjects() {
        ExpenseParticipantsId ep1 = new ExpenseParticipantsId(1, "user");
        ExpenseParticipantsId ep2 = new ExpenseParticipantsId(1,"user");
        assertEquals(ep1.hashCode(), ep2.hashCode(), "HashCodes should be equal for equal objects");
    }

    @Test
    void testEquals_Null() {
        ExpenseParticipantsId id1 = new ExpenseParticipantsId(1, "user1");
        assertFalse(id1.equals(null), "Object should not be equal to null");
    }

    @Test
    void testHashCode_forSingleObject() {
        ExpenseParticipantsId ep = new ExpenseParticipantsId(1, "user");
        assertEquals(ep.hashCode(), ep.hashCode(),"Same hash code must be generated for the same object");
    }

    @Test
    void testSettersAndGetters () {

        ExpenseParticipantsId ep = new ExpenseParticipantsId(1, "user");
        int expenseId = 2;
        String username = "testuser";
        ep.setExpenseId(expenseId);
        ep.setUsername(username);

        assertEquals(expenseId, ep.getExpenseId(), "ExpenseId must be equal");
        assertEquals(username, ep.getUsername(), "Usernames must be equal");
    }
}
