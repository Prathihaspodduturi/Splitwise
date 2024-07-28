package com.PrathihasProjects.PrathihasSplitwise.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroupsTest {

    @Test
    void testGroupsConstructor() {
        User createdBy = new User("testUser", "testPassword");
        Date dateCreated = new Date();
        String groupName = "Test Group";
        String groupDescription = "Description for Test Group";

        Groups group = new Groups(groupName, groupDescription, dateCreated, createdBy);

        assertEquals("Test Group", group.getGroupName(), "testing groupname in constructor");
        assertEquals("Description for Test Group", group.getGroupDescription(), "testing group description in constructor");
        assertEquals(dateCreated, group.getDateCreated(), "testing date created in constructor");
        assertEquals(createdBy, group.getCreatedBy(), "testing created by in constructor");
    }


    @Test
    void testSetAndGetId() {
        Groups group = new Groups();
        group.setId(1);
        assertEquals(1, group.getId(), "testing set and Get iD");
    }

    @Test
    void testSetAndGetGroupName() {
        Groups group = new Groups();
        group.setGroupName("New Group Name");
        assertEquals("New Group Name", group.getGroupName(), "testing set and get GroupName");
    }

    @Test
    void testSetandGetGroupDescription() {
        Groups group = new Groups();
        group.setGroupDescription("New Group Description");
        assertEquals("New Group Description", group.getGroupDescription(), "testing set and get group description");
    }

    @Test
    void testSetandGetDateCreated() {
        Groups group = new Groups();
        Date dateCreated = new Date();
        group.setDateCreated(dateCreated);
        assertEquals(dateCreated, group.getDateCreated(), "testing set and Get date crated");
    }

    @Test
    void testSetandGetCreatedBy() {
        Groups group = new Groups();
        User createdBy = new User("testUser", "testPassword");
        group.setCreatedBy(createdBy);
        assertEquals(createdBy, group.getCreatedBy(), "testing set and Get created by");
    }

    @Test
    void testSetandGetDeleted() {
        Groups group = new Groups();
        group.setDeleted(true);
        assertTrue(group.isDeleted(), "testing set and Get deleted");
    }

    @Test
    void testSetandGetSettledUp() {
        Groups group = new Groups();
        group.setSettledUp(true);
        assertTrue(group.isSettledUp(), "testing set and Get settledgroup");
    }

    @Test
    void testSetandGetSettledDate() {
        Groups group = new Groups();
        Date settledDate = new Date();
        group.setSettledDate(settledDate);
        assertEquals(settledDate, group.getSettledDate(), "testing set and Get settledDate");
    }

    @Test
    void testSetandGetDeletedDate() {
        Groups group = new Groups();
        Date deletedDate = new Date();
        group.setDeletedDate(deletedDate);
        assertEquals(deletedDate, group.getDeletedDate(), "testing set and Get deleted date");
    }

    @Test
    void testSetandGetDeletedBy() {
        Groups group = new Groups();
        User deletedBy = new User("deletedUser", "testPassword");
        group.setDeletedBy(deletedBy);
        assertEquals(deletedBy, group.getDeletedBy(), "testing set and Get deleted by");
    }

    @Test
    void testSetandGetSettledBy() {
        Groups group = new Groups();
        User settledBy = new User("settledUser", "testPassword");
        group.setSettledBy(settledBy);
        assertEquals(settledBy, group.getSettledBy(), "testing set and Get settled By");
    }

    @Test
    void testToString() {
        User createdBy = new User("testUser", "testPassword");
        Date dateCreated = new Date();
        Groups group = new Groups("Test Group", "Group Description", dateCreated, createdBy);

        String expected = "Groups{" +
                "id=" + group.getId() +
                ", groupName='Test Group'" +
                ", groupDescription='Group Description'" +
                ", dateCreated=" + dateCreated +
                ", settledDate=" + group.getSettledDate() +
                ", deletedDate=" + group.getDeletedDate() +
                ", settledUp=" + group.isSettledUp() +
                ", createdBy=" + createdBy +
                ", deleted=" + group.isDeleted() +
                ", deletedBy=" + group.getDeletedBy() +
                ", settledBy=" + group.getSettledBy() +
                '}';

        assertEquals(expected, group.toString(), "testing to String");
    }
}
