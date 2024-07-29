package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.compositeKey.ExpenseParticipantsId;
import com.PrathihasProjects.PrathihasSplitwise.entity.ExpenseParticipants;
import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.aspectj.lang.annotation.After;
import org.aspectj.weaver.patterns.IVerificationRequired;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ExpenseParticipantsDAOImplTest {

    AutoCloseable openMocks;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ExpenseParticipantsDAOImpl expenseParticipantsDAO;

    @BeforeEach
    void setUp () {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        openMocks.close();
    }

    @Test
    void saveTest_NotExists()
    {
        ExpenseParticipants participants = new ExpenseParticipants();
        participants.setId(null);

        expenseParticipantsDAO.save(participants);

        verify(entityManager).persist(participants);
    }

    @Test
    void saveTest_Exists()
    {
        ExpenseParticipants participants = new ExpenseParticipants();

        int expenseId = 1;
        String username = "testUser";

        ExpenseParticipantsId id = new ExpenseParticipantsId(expenseId, username);

        participants.setId(id);

        expenseParticipantsDAO.save(participants);

        verify(entityManager).merge(participants);
    }

    @Test
    void findParticipantTest () {

        int expenseId = 1;
        String username = "testUser";

        ExpenseParticipants expectedParticipant = new ExpenseParticipants();

        TypedQuery<ExpenseParticipants> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(ExpenseParticipants.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("expenseId", expenseId)).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", username)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(expectedParticipant);

        ExpenseParticipants result = expenseParticipantsDAO.findParticipant(expenseId, username);

        assertEquals(result, expectedParticipant, "expense participants must be equal");

        verify(entityManager).createQuery(anyString(), eq(ExpenseParticipants.class));
        verify(typedQuery).setParameter("expenseId", expenseId);
        verify(typedQuery).setParameter("username", username);
        verify(typedQuery).getSingleResult();

    }

    @Test
    void findParticipantTest_throwException () {

        int expenseId = 1;
        String username = "testUser";

        ExpenseParticipants expectedParticipant = new ExpenseParticipants();

        TypedQuery<ExpenseParticipants> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(ExpenseParticipants.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("expenseId", expenseId)).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", username)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenThrow(new NoResultException());

        ExpenseParticipants result = expenseParticipantsDAO.findParticipant(expenseId, username);

        assertNull(result, "expense participants must be null");

        verify(entityManager).createQuery(anyString(), eq(ExpenseParticipants.class));
        verify(typedQuery).setParameter("expenseId", expenseId);
        verify(typedQuery).setParameter("username", username);
        verify(typedQuery).getSingleResult();

    }

    @Test
    void findByExpenseIdTest () {

        int expenseId = 1;

        ExpenseParticipants expenseParticipants1 = new ExpenseParticipants();
        ExpenseParticipants expenseParticipants2 = new ExpenseParticipants();

        List<ExpenseParticipants> expectedExpenseParticipants = Arrays.asList(expenseParticipants1, expenseParticipants2);

        TypedQuery<ExpenseParticipants> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(ExpenseParticipants.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("expenseId", expenseId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedExpenseParticipants);

        List<ExpenseParticipants>result = expenseParticipantsDAO.findByExpenseId(expenseId);

        assertTrue(result.containsAll(expectedExpenseParticipants), "result must have all expense participants from expectedExpenseParticipants");

        verify(entityManager).createQuery(anyString(), eq(ExpenseParticipants.class));
        verify(typedQuery).setParameter("expenseId", expenseId);
        verify(typedQuery).getResultList();
    }

    @Test
    void updateExpenseParticipantsTest () {

        ExpenseParticipants participant = new ExpenseParticipants();

        expenseParticipantsDAO.updateExpenseParticipants(participant);

        verify(entityManager).merge(participant);

    }

    @Test
    void deleteParticipantByeExpenseAndUser () {

        int expenseId = 1;
        String username = "testUser";

        ExpenseParticipants participant = new ExpenseParticipants();

        TypedQuery<ExpenseParticipants> typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(ExpenseParticipants.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("expenseId", expenseId)).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", username)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(participant);

        expenseParticipantsDAO.deleteParticipantByExpenseAndUser(expenseId, username);

        verify(entityManager).remove(participant);

    }
}
