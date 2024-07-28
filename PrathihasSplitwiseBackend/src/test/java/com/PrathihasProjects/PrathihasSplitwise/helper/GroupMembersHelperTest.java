package com.PrathihasProjects.PrathihasSplitwise.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GroupMembersHelperTest {

    private GroupMembersHelper groupMembersHelper;
    private String username;
    private int groupId;
    private String addedBy;
    private Date addedDate;


    @BeforeEach
    public void setUp()
    {
        username = "testUser";
        groupId = 1;
        addedBy = "admin";
        addedDate = new Date();
        groupMembersHelper = new GroupMembersHelper(username, groupId, addedBy, addedDate);
    }

    @Test
    void testConstructor() {
        assertEquals(username, groupMembersHelper.getUsername());
        assertEquals(groupId, groupMembersHelper.getGroupId());
        assertEquals(addedBy, groupMembersHelper.getAddedBy());
        assertEquals(addedDate, groupMembersHelper.getAddedDate());
        assertNull(groupMembersHelper.getRemovedBy());
        assertNull(groupMembersHelper.getRemovedDate());
    }

    @Test
    public void testSetAndGetUsername() {
        String newUsername = "newUser";
        groupMembersHelper.setUsername(newUsername);
        assertEquals(newUsername, groupMembersHelper.getUsername());
    }

    @Test
    public void testSetAndGetGroupId() {
        int newGroupId = 2;
        groupMembersHelper.setGroupId(newGroupId);
        assertEquals(newGroupId, groupMembersHelper.getGroupId());
    }

    @Test
    public void testSetAndGetRemovedBy() {
        String removedBy = "moderator";
        groupMembersHelper.setRemovedBy(removedBy);
        assertEquals(removedBy, groupMembersHelper.getRemovedBy());
    }

    @Test
    public void testSetAndGetAddedBy() {
        String newAddedBy = "superadmin";
        groupMembersHelper.setAddedBy(newAddedBy);
        assertEquals(newAddedBy, groupMembersHelper.getAddedBy());
    }

    @Test
    public void testSetAndGetAddedDate() {
        Date newAddedDate = new Date();
        groupMembersHelper.setAddedDate(newAddedDate);
        assertEquals(newAddedDate, groupMembersHelper.getAddedDate());
    }

    @Test
    public void testSetAndGetRemovedDate() {
        Date removedDate = new Date();
        groupMembersHelper.setRemovedDate(removedDate);
        assertEquals(removedDate, groupMembersHelper.getRemovedDate());
    }

    @Test
    public void testToString() {
        String expectedString = "GroupMembersHelper{" +
                "username='" + username + '\'' +
                ", groupId=" + groupId +
                ", removedBy='" + null + '\'' +
                ", addedBy='" + addedBy + '\'' +
                ", addedDate=" + addedDate +
                ", removedDate=" + null +
                '}';
        assertEquals(expectedString, groupMembersHelper.toString());
    }

}
