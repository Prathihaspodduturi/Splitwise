package com.PrathihasProjects.PrathihasSplitwise.compositeKey;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupMembersIdTest {

    @Test
    void testEmptyCOnstructor ()
    {
       GroupMembersId gm = new GroupMembersId();

        assertNull(gm.getUsername(), "username must be null for empty Constructor");
        assertEquals(0, gm.getGroupId(), "GroupId must be null for empty Constructor");
    }

    @Test
    void testEquals_SameObject () {

        GroupMembersId gm = new GroupMembersId(1, "user");
        assertTrue(gm.equals(gm), "Object should be equal to itself");
    }

    @Test
    void testEquals_DifferentObjectWithEqualData () {

        GroupMembersId gm1 = new GroupMembersId(1, "user");
        GroupMembersId gm2 = new GroupMembersId(1, "user");
        assertTrue(gm1.equals(gm2), "Objects with same data should be equal");
    }

    @Test
    void testEquals_DifferentData () {
        GroupMembersId gm1 = new GroupMembersId(1, "user");
        GroupMembersId gm2 = new GroupMembersId(1, "user1");

        assertFalse(gm1.equals(gm2), "Objects with different data should not be equal");
    }

    @Test
    void testHashCode_forEqualObjects() {

        GroupMembersId gm1 = new GroupMembersId(1, "user");
        GroupMembersId gm2 = new GroupMembersId(1, "user");

        assertEquals(gm1.hashCode(), gm2.hashCode(), "HashCodes should be equal for equal objects");
    }

    @Test
    void testEquals_Null() {
        GroupMembersId gm1 = new GroupMembersId(1, "user");
        assertFalse(gm1.equals(null), "Object should not be equal to null");
    }

    @Test
    void testHashCode_forSingleObject() {
        GroupMembersId gm1 = new GroupMembersId(1, "user");

        assertEquals(gm1.hashCode(), gm1.hashCode(),"Same hash code must be generated for the same object");
    }

    @Test
    void testSettersAndGetters () {
        GroupMembersId gm = new GroupMembersId(1, "user");

        int groupId = 2;
        String username = "testuser";
        gm.setGroupId(groupId);
        gm.setUsername(username);

        assertEquals(groupId, gm.getGroupId(), "ExpenseId must be equal");
        assertEquals(username, gm.getUsername(), "Usernames must be equal");
    }
}
