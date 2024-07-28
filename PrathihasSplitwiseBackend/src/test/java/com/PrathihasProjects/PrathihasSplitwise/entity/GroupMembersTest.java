package com.PrathihasProjects.PrathihasSplitwise.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupMembersTest {

    @Test
    void testGroupMembers()
    {
        Groups group = new Groups("Test Group", "Group Description", new Date(), new User("testUser", "password"));
        User user = new User("testUser1", "password");
        User addedBy = new User("addedByUser", "password");
        Date addedDate = new Date();

        GroupMembers groupMember = new GroupMembers(group, user, addedDate, addedBy);

        assertEquals(group, groupMember.getGroup(), "testing group in constructor");
        assertEquals(user, groupMember.getUser(), "testing user in constructor");
        assertEquals(addedDate, groupMember.getAddedDate(), "testing addedDate in constructor");
        assertEquals(addedBy, groupMember.getAddedBy(), "testing addedBy in constructor");
    }

    @Test
    void testSetAndGetId()
    {
        GroupMembers groupMember = new GroupMembers();
        groupMember.setId(1);
        assertEquals(1, groupMember.getId(), "testing set and get of group member id ");
    }

    @Test
    void testSetAndGetGroup() {
        GroupMembers groupMember = new GroupMembers();
        Groups group = new Groups("Test Group", "Group Description", new Date(), new User("testUser1", "password"));
        groupMember.setGroup(group);
        assertEquals(group, groupMember.getGroup(), "testing set and get of group in GroupMember");
    }

    @Test
    void testSetAndGetUser() {
        GroupMembers groupMember = new GroupMembers();
        User user = new User("testUser2", "password");
        groupMember.setUser(user);
        assertEquals(user, groupMember.getUser(), "testing set and get User in GroupMember");
    }

    @Test
    void testSetAndGetRemovedBy() {
        GroupMembers groupMember = new GroupMembers();
        User removedBy = new User("removedByUser", "password");
        groupMember.setRemovedBy(removedBy);
        assertEquals(removedBy, groupMember.getRemovedBy(), "testing set and get RemovedBy in GroupMember");
    }

    @Test
    void testSetAndGetRemovedDate() {
        GroupMembers groupMember = new GroupMembers();
        Date removedDate = new Date();
        groupMember.setRemovedDate(removedDate);
        assertEquals(removedDate, groupMember.getRemovedDate(), "testing set and get RemovedDate in GroupMember");
    }

    @Test
    void testSetAndGetAddedDate() {
        GroupMembers groupMember = new GroupMembers();
        Date addedDate = new Date();
        groupMember.setAddedDate(addedDate);
        assertEquals(addedDate, groupMember.getAddedDate(), "testing set and get AddedDate in GroupMember");
    }

    @Test
    void testSetAndGetAddedBy() {
        GroupMembers groupMember = new GroupMembers();
        User addedBy = new User("addedByUser", "password");
        groupMember.setAddedBy(addedBy);
        assertEquals(addedBy, groupMember.getAddedBy(), "testing set and get AddedBy in GroupMember");
    }

    @Test
    void testToString() {
        Groups group = new Groups("Test Group", "Group Description", new Date(), new User("testUser1", "password"));
        User user = new User("testUser2", "password");
        User removedBy = new User("removedByUser", "password");
        Date removedDate = new Date();
        Date addedDate = new Date();
        User addedBy = new User("addedByUser", "password");

        GroupMembers groupMember = new GroupMembers();
        groupMember.setGroup(group);
        groupMember.setUser(user);
        groupMember.setRemovedBy(removedBy);
        groupMember.setRemovedDate(removedDate);
        groupMember.setAddedDate(addedDate);
        groupMember.setAddedBy(addedBy);

        String expected = "GroupMembers{" +
                "id=" + groupMember.getId() +
                ", group=" + group +
                ", user=" + user +
                ", removedBy=" + removedBy +
                ", removedDate=" + removedDate +
                ", addedDate=" + addedDate +
                ", addedBy=" + addedBy +
                '}';

        assertEquals(expected, groupMember.toString());
    }

}
