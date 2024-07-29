package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GroupsDAOImplTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private GroupsDAOImpl groupsDAO;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        openMocks.close();
    }

    @Test
    void saveTest ()
    {
        Groups group = new Groups();

        groupsDAO.save(group);

        verify(entityManager).persist(group);
    }

    @Test
    void updateGroupNameTest ()
    {
        Groups group = new Groups();

        groupsDAO.updateGroupName(group);

        verify(entityManager).merge(group);
    }

    @Test
    void findGroupByIdTest ()
    {
        int id = 1;

        Groups expectedGroup = new Groups();
        expectedGroup.setId(id);

        when(entityManager.find(Groups.class, id)).thenReturn(expectedGroup);

        Groups returnedGroup = groupsDAO.findGroupById(id);

        verify(entityManager).find(Groups.class, id);

        assertSame(expectedGroup, returnedGroup, "The groups must be equal");

    }

    @Test
    void deleteGroupByIdTest ()
    {
        int groupId = 1;
        String userName = "testName";
        Date deletedDate = new Date();

        User expectedUser = new User();
        expectedUser.setUsername(userName);

        when(entityManager.find(User.class, userName)).thenReturn(expectedUser);

        Groups group = new Groups();
        group.setId(groupId);

        when(entityManager.find(Groups.class, groupId)).thenReturn(group);

        boolean result = groupsDAO.deletegroupById(groupId, userName, deletedDate);

        verify(entityManager).find(User.class, userName);
        verify(entityManager).find(Groups.class, groupId);

        assertTrue(group.isDeleted(), "group must be marked as deleted");
        assertEquals(expectedUser, group.getDeletedBy(), "Deleted by user should match with expected user");
        assertEquals(deletedDate, group.getDeletedDate(), "Deleted date must be same as group's get deleted date");

        assertTrue(result, "The method should return true if the group was successfully marked as deleted");
    }

    @Test
    void deleteGroupByIdTest_WhenGroupNotFound() {
        // Setup
        int groupId = 1;
        String userName = "testName";
        Date deletionDate = new Date();

        // Mocking User
        User expectedUser = new User();
        expectedUser.setUsername(userName);
        when(entityManager.find(User.class, userName)).thenReturn(expectedUser);

        // Mocking Group not found
        when(entityManager.find(Groups.class, groupId)).thenReturn(null);

        // Method invocation
        boolean result = groupsDAO.deletegroupById(groupId, userName, deletionDate);

        // Verify interactions
        verify(entityManager).find(User.class, userName);
        verify(entityManager).find(Groups.class, groupId);

        // Assert result
        assertFalse(result, "The method should return false if no group was found");
    }


    @Test
    void settleGroupByIdTest ()
    {
        int groupId = 1;
        String userName = "testName";
        Date settledDate = new Date();

        User expectedUser = new User();
        expectedUser.setUsername(userName);

        when(entityManager.find(User.class, userName)).thenReturn(expectedUser);

        Groups group = new Groups();
        group.setId(groupId);

        when(entityManager.find(Groups.class, groupId)).thenReturn(group);

        boolean result = groupsDAO.settlegroupById(groupId, userName, settledDate);

        verify(entityManager).find(User.class, userName);
        verify(entityManager).find(Groups.class, groupId);

        assertTrue(group.isSettledUp(), "group must be marked as settled");
        assertEquals(expectedUser, group.getSettledBy(), "Settled by user should match with expected user");
        assertEquals(settledDate, group.getSettledDate(), "Settled date must be same as group's Settled date");

        assertTrue(result, "The method should return true if the group was successfully marked as Settled");
    }

    @Test
    void settledGroupByIdTest_WhenGroupNotFound() {
        // Setup
        int groupId = 1;
        String userName = "testName";
        Date settledDate = new Date();

        // Mocking User
        User expectedUser = new User();
        expectedUser.setUsername(userName);
        when(entityManager.find(User.class, userName)).thenReturn(expectedUser);

        // Mocking Group not found
        when(entityManager.find(Groups.class, groupId)).thenReturn(null);

        // Method invocation
        boolean result = groupsDAO.settlegroupById(groupId, userName, settledDate);

        // Verify interactions
        verify(entityManager).find(User.class, userName);
        verify(entityManager).find(Groups.class, groupId);

        // Assert result
        assertFalse(result, "The method should return false if no group was found");
    }

    @Test
    void restoreGroupTest ()
    {
        int groupId = 1;

        Groups group = new Groups();
        group.setId(groupId);

        when(entityManager.find(Groups.class, groupId)).thenReturn(group);

        boolean result = groupsDAO.restoreGroup(groupId);

        verify(entityManager).find(Groups.class, groupId);

        assertFalse(group.isDeleted(), "group must be marked as deleted");
        assertNull(group.getDeletedBy(), "Group deleted by value must be nullr");
        assertNull(group.getDeletedDate(), "Group deleted must be null");

        assertTrue(result, "The method should return true if the group was successfully marked as deleted");
    }

    @Test
    void restoreGroupTest_WhenGroupNotFound() {
        // Setup
        int groupId = 1;

        // Mocking Group not found
        when(entityManager.find(Groups.class, groupId)).thenReturn(null);

        // Method invocation
        boolean result = groupsDAO.restoreGroup(groupId);

        // Verify interactions
        verify(entityManager).find(Groups.class, groupId);

        // Assert result
        assertFalse(result, "The method should return false if no group was found");
    }

}
