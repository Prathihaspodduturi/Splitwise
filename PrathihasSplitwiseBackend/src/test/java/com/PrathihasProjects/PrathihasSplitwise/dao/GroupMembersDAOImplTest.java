package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.swing.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroupMembersDAOImplTest {

    AutoCloseable openMocks;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private GroupMembersDAOImpl groupMembersDAO;

    @BeforeEach
    void setUp()
    {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks () throws Exception
    {
        openMocks.close();
    }

    @Test
    void saveTest_MemberExists() {

        GroupMembers members = new GroupMembers();

        User user = new User();
        user.setUsername("prathihas");

        Groups group = new Groups();
        group.setId(1);

        members.setUser(user);
        members.setGroup(group);

        TypedQuery<GroupMembers> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(GroupMembers.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new GroupMembers());

        groupMembersDAO.save(members);

        verify(entityManager).merge(members);
    }

    @Test
    void saveTest_MemberNotExists() {

        GroupMembers members = new GroupMembers();

        User user = new User();
        user.setUsername("prathihas");

        Groups group = new Groups();
        group.setId(1);

        members.setUser(user);
        members.setGroup(group);

        TypedQuery<GroupMembers> query = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(GroupMembers.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(null);

        groupMembersDAO.save(members);

        verify(entityManager).persist(members);
    }

    @Test
    void findGroupsOfUserTest () {

        String username = "testUser";
        GroupMembers member1 = new GroupMembers();
        GroupMembers member2 = new GroupMembers();
        Groups group1 = new Groups();
        Groups group2 = new Groups();
        group1.setId(1); // Assuming there's a method to set IDs
        group2.setId(2);
        member1.setGroup(group1);
        member2.setGroup(group2);

        List<GroupMembers> mockMembers = Arrays.asList(member1, member2);
        TypedQuery<GroupMembers> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(GroupMembers.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", username)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(mockMembers);

        List<Groups> groups = groupMembersDAO.findGroupsOfUser(username);
        assertNotNull(groups);
        assertEquals(2, groups.size());
        assertTrue(groups.contains(group1) && groups.contains(group2), "Should contain both groups");

        // Verify interactions
        verify(entityManager).createQuery("SELECT gm FROM GroupMembers gm WHERE gm.user.username = :username", GroupMembers.class);
        verify(typedQuery).setParameter("username", username);
        verify(typedQuery).getResultList();

    }

    @Test
    void findMembersByGroupIdTest () {

        int groupId = 1;
        String username1 = "testUser1";
        String username2 = "testUser2";

        List<String> userNames = Arrays.asList(username1, username2);
        TypedQuery<String> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(String.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("groupId", groupId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(userNames);

        List<String> result = groupMembersDAO.findMembersByGroupId(groupId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(username1) && result.contains(username2), "Should contain both usernames");

        // Verify interactions
        verify(entityManager).createQuery("SELECT gm.user.username FROM GroupMembers gm WHERE gm.group.id = :groupId AND gm.addedDate IS NOT NULL AND gm.removedDate IS NULL", String.class);
        verify(typedQuery).setParameter("groupId", groupId);
        verify(typedQuery).getResultList();

    }

    @Test
    void isMemberTest_memberExists () {

        String userName = "testUser";
        int groupId = 1;

        TypedQuery<GroupMembers> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(GroupMembers.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", userName)).thenReturn(typedQuery);
        when(typedQuery.setParameter("groupId", groupId)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new GroupMembers());

        boolean result = groupMembersDAO.isMember(userName, groupId);

        assertTrue(result);

        verify(entityManager).createQuery(anyString(), eq(GroupMembers.class));
        verify(typedQuery).setParameter("groupId", groupId);
        verify(typedQuery).setParameter("username", userName);
        verify(typedQuery).getSingleResult();
    }

    @Test
    void isMemberTest_MemberNotExists () {

        String userName = "testUser";
        int groupId = 1;

        TypedQuery<GroupMembers> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(GroupMembers.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", userName)).thenReturn(typedQuery);
        when(typedQuery.setParameter("groupId", groupId)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(null);

        boolean result = groupMembersDAO.isMember(userName, groupId);

        assertFalse(result);

        verify(entityManager).createQuery(anyString(), eq(GroupMembers.class));
        verify(typedQuery).setParameter("groupId", groupId);
        verify(typedQuery).setParameter("username", userName);
        verify(typedQuery).getSingleResult();
    }

    @Test
    void isMemberTest_ThrowException () {

        String userName = "testUser";
        int groupId = 1;

        TypedQuery<GroupMembers> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(GroupMembers.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", userName)).thenReturn(typedQuery);
        when(typedQuery.setParameter("groupId", groupId)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenThrow(new NoResultException());

        boolean result = groupMembersDAO.isMember(userName, groupId);

        assertFalse(result);

        verify(entityManager).createQuery(anyString(), eq(GroupMembers.class));
        verify(typedQuery).setParameter("groupId", groupId);
        verify(typedQuery).setParameter("username", userName);
        verify(typedQuery).getSingleResult();
    }

    @Test
    void isOldMemberTest_memberExists () {

        String userName = "testUser";
        int groupId = 1;

        TypedQuery<GroupMembers> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(GroupMembers.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", userName)).thenReturn(typedQuery);
        when(typedQuery.setParameter("groupId", groupId)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new GroupMembers());

        boolean result = groupMembersDAO.isOldMember(userName, groupId);

        assertTrue(result);

        verify(entityManager).createQuery(anyString(), eq(GroupMembers.class));
        verify(typedQuery).setParameter("groupId", groupId);
        verify(typedQuery).setParameter("username", userName);
        verify(typedQuery).getSingleResult();
    }

    @Test
    void isOldMemberTest_MemberNotExists () {

        String userName = "testUser";
        int groupId = 1;

        TypedQuery<GroupMembers> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(GroupMembers.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", userName)).thenReturn(typedQuery);
        when(typedQuery.setParameter("groupId", groupId)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(null);

        boolean result = groupMembersDAO.isOldMember(userName, groupId);

        assertFalse(result);

        verify(entityManager).createQuery(anyString(), eq(GroupMembers.class));
        verify(typedQuery).setParameter("groupId", groupId);
        verify(typedQuery).setParameter("username", userName);
        verify(typedQuery).getSingleResult();
    }

    @Test
    void isOldMemberTest_ThrowException () {

        String userName = "testUser";
        int groupId = 1;

        TypedQuery<GroupMembers> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(GroupMembers.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", userName)).thenReturn(typedQuery);
        when(typedQuery.setParameter("groupId", groupId)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenThrow(new NoResultException());

        boolean result = groupMembersDAO.isOldMember(userName, groupId);

        assertFalse(result);

        verify(entityManager).createQuery(anyString(), eq(GroupMembers.class));
        verify(typedQuery).setParameter("groupId", groupId);
        verify(typedQuery).setParameter("username", userName);
        verify(typedQuery).getSingleResult();
    }

    @Test
    void getDetailsTest () {

        String userName = "testUser";
        int groupId = 1;

        GroupMembers expectedGroupMember = new GroupMembers();

        TypedQuery<GroupMembers> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(GroupMembers.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", userName)).thenReturn(typedQuery);
        when(typedQuery.setParameter("groupId", groupId)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(expectedGroupMember);

        GroupMembers result = groupMembersDAO.getDetails(groupId, userName);

        assertNotNull(result);
        assertEquals(expectedGroupMember, result, "Should return the expected GroupMembers object");

        verify(entityManager).createQuery(anyString(), eq(GroupMembers.class));
        verify(typedQuery).setParameter("groupId", groupId);
        verify(typedQuery).setParameter("username", userName);
        verify(typedQuery).getSingleResult();
    }

    @Test
    void getDetailsTest_throwsException () {

        String userName = "testUser";
        int groupId = 1;

        TypedQuery<GroupMembers> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(GroupMembers.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", userName)).thenReturn(typedQuery);
        when(typedQuery.setParameter("groupId", groupId)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenThrow(new NoResultException());

        GroupMembers result = groupMembersDAO.getDetails(groupId, userName);

        assertNull(result);

        verify(entityManager).createQuery(anyString(), eq(GroupMembers.class));
        verify(typedQuery).setParameter("groupId", groupId);
        verify(typedQuery).setParameter("username", userName);
        verify(typedQuery).getSingleResult();
    }

}
