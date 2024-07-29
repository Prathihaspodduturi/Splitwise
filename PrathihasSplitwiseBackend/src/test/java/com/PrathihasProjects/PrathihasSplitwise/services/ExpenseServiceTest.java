package com.PrathihasProjects.PrathihasSplitwise.services;

import com.PrathihasProjects.PrathihasSplitwise.dao.ExpenseParticipantsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpensesDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {

    AutoCloseable openMocks;
    @Mock
    private GroupsDAOImpl theGroupsDAOImpl;

    @Mock
    private ExpensesDAOImpl expensesDAO;

    @Mock
    private ExpenseParticipantsDAOImpl expenseParticipantsDAO;

    @Mock
    private GroupMembersDAOImpl groupMembersDAO;

    @InjectMocks
    private ExpenseService expenseService;

    @BeforeEach
    void setUp ()
    {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        openMocks.close();
    }

    @Test
    void testGetExpenseDetails_GroupNotFound() {
        when(theGroupsDAOImpl.findGroupById(123)).thenReturn(null);
        Map<String, Object> result = expenseService.getExpenseDetails(1, 123, "user");
        assertNull(result, "Expected null when no group is found");
        //verify(expensesDAO, never()).findExpenseById(anyInt()); // Ensures we never proceed to expense DAO if group is not found
    }


    @Test
    void testGetExpenseDetails_ExpenseNotFound() {
        Groups mockGroup = new Groups(); // Assuming constructor creates a valid empty group
        when(theGroupsDAOImpl.findGroupById(123)).thenReturn(mockGroup);
        when(expensesDAO.findExpenseById(1)).thenReturn(null);

        Map<String, Object> result = expenseService.getExpenseDetails(1, 123, "user");
        assertNull(result, "Expected null when no expense is found");
        //verify(expenseParticipantsDAO, never()).findByExpenseId(anyInt()); // Ensures no participant lookup if expense is not found
    }

    @Test
    void testGetExpenseDetails_validDetails() {

        int groupId = 1, expenseId = 1;
        String username = "testuser";

        Groups group = new Groups();
        group.setId(groupId);
        when(theGroupsDAOImpl.findGroupById(1)).thenReturn(group);

        Expenses expense = new Expenses();
        User addedBy = new User();
        addedBy.setUsername("user1");
        expense.setExpenseName("Test Expense");
        expense.setAmount(new BigDecimal("100.00"));
        expense.setDateCreated(new Date());
        expense.setAddedBy(addedBy);
        expense.setPayment(false);
        when(expensesDAO.findExpenseById(1)).thenReturn(expense);

        GroupMembers gmDetails = new GroupMembers();
        gmDetails.setRemovedBy(null);
        when(groupMembersDAO.getDetails(groupId, username)).thenReturn(gmDetails);

        ExpenseParticipants participant1 = new ExpenseParticipants();
        User participantUser1 = new User();
        participantUser1.setUsername("participant1");
        participant1.setUser(participantUser1);
        participant1.setAmountpaid(new BigDecimal("50.00"));
        participant1.setAmountOwed(new BigDecimal("0.00"));

        ExpenseParticipants participant2 = new ExpenseParticipants();
        User participantUser2 = new User();
        participantUser2.setUsername("participant2");
        participant2.setUser(participantUser2);
        participant2.setAmountpaid(new BigDecimal("0.00"));
        participant2.setAmountOwed(new BigDecimal("50.00"));

        List<ExpenseParticipants> participants = Arrays.asList(participant1, participant2);
        when(expenseParticipantsDAO.findByExpenseId(expenseId)).thenReturn(participants);

        Map<String, Object> expectedExpenseDetails = new HashMap<>();

        expectedExpenseDetails.put("expenseName", "Test Expense");
        expectedExpenseDetails.put("amount", new BigDecimal("100.00"));
        expectedExpenseDetails.put("dateCreated", expense.getDateCreated());
        expectedExpenseDetails.put("addedBy", "user1");
        expectedExpenseDetails.put("isPayment", false);
        expectedExpenseDetails.put("isDeleted", false);

        List<Map<String, Object>> participantDetails = new ArrayList<>();
        Map<String, Object> participantDetail1 = new HashMap<>();
        participantDetail1.put("username", "participant1");
        participantDetail1.put("amountPaid", new BigDecimal("50.00"));
        participantDetail1.put("amountOwed", new BigDecimal("0.00"));

        Map<String, Object> participantDetail2 = new HashMap<>();
        participantDetail2.put("username", "participant2");
        participantDetail2.put("amountPaid", new BigDecimal("0.00"));
        participantDetail2.put("amountOwed", new BigDecimal("50.00"));
        participantDetail2.put("isChecked", true);

        participantDetails.add(participantDetail1);
        participantDetails.add(participantDetail2);

        expectedExpenseDetails.put("participants", participantDetails);
        expectedExpenseDetails.put("gmRemovedDate", null);

        Map<String, Object> actualExpenseDetails = expenseService.getExpenseDetails(expenseId, groupId, username);

        assertEquals(expectedExpenseDetails, actualExpenseDetails);

        verify(theGroupsDAOImpl).findGroupById(groupId);
        verify(expensesDAO).findExpenseById(expenseId);
        verify(expenseParticipantsDAO).findByExpenseId(expenseId);
        verify(groupMembersDAO).getDetails(groupId, username);
    }

    @Test
    void testGetExpenseDetails_validDetailsWithAllValuesTrue() {

        int groupId = 1, expenseId = 1;
        String username = "testuser";

        Groups group = new Groups();
        group.setId(groupId);
        when(theGroupsDAOImpl.findGroupById(1)).thenReturn(group);

        Expenses expense = new Expenses();
        User addedBy = new User();
        addedBy.setUsername("user1");

        User updatedByUser = new User();
        updatedByUser.setUsername("updated user");

        User deletedByUser = new User();
        deletedByUser.setUsername("deleted user");

        expense.setExpenseName("Test Expense");
        expense.setAmount(new BigDecimal("100.00"));
        expense.setDateCreated(new Date());
        expense.setLastUpdatedDate(new Date());
        expense.setAddedBy(addedBy);
        expense.setUpdatedBy(updatedByUser);
        expense.setDeletedBy(deletedByUser);
        expense.setDeletedDate(new Date());
        expense.setDeleted(true);
        expense.setPayment(false);
        when(expensesDAO.findExpenseById(1)).thenReturn(expense);

        GroupMembers gmDetails = new GroupMembers();
        User removedByUser = new User();
        removedByUser.setUsername("removed by");

        gmDetails.setRemovedBy(removedByUser);
        gmDetails.setRemovedDate(new Date());

        when(groupMembersDAO.getDetails(groupId, username)).thenReturn(gmDetails);

        ExpenseParticipants participant1 = new ExpenseParticipants();
        User participantUser1 = new User();
        participantUser1.setUsername("participant1");
        participant1.setUser(participantUser1);
        participant1.setAmountpaid(new BigDecimal("50.00"));
        participant1.setAmountOwed(new BigDecimal("0.00"));

        ExpenseParticipants participant2 = new ExpenseParticipants();
        User participantUser2 = new User();
        participantUser2.setUsername("participant2");
        participant2.setUser(participantUser2);
        participant2.setAmountpaid(new BigDecimal("0.00"));
        participant2.setAmountOwed(new BigDecimal("50.00"));

        List<ExpenseParticipants> participants = Arrays.asList(participant1, participant2);
        when(expenseParticipantsDAO.findByExpenseId(expenseId)).thenReturn(participants);

        Map<String, Object> expectedExpenseDetails = new HashMap<>();

        expectedExpenseDetails.put("expenseName", "Test Expense");
        expectedExpenseDetails.put("amount", new BigDecimal("100.00"));
        expectedExpenseDetails.put("dateCreated", expense.getDateCreated());
        expectedExpenseDetails.put("addedBy", "user1");
        expectedExpenseDetails.put("isPayment", false);
        expectedExpenseDetails.put("isDeleted", true);
        expectedExpenseDetails.put("updatedBy", expense.getUpdatedBy().getUsername());
        expectedExpenseDetails.put("lastUpdatedDate", expense.getLastUpdatedDate());
        expectedExpenseDetails.put("deletedBy", expense.getDeletedBy().getUsername());
        expectedExpenseDetails.put("deletedDate", expense.getDeletedDate());

        List<Map<String, Object>> participantDetails = new ArrayList<>();
        Map<String, Object> participantDetail1 = new HashMap<>();
        participantDetail1.put("username", "participant1");
        participantDetail1.put("amountPaid", new BigDecimal("50.00"));
        participantDetail1.put("amountOwed", new BigDecimal("0.00"));

        Map<String, Object> participantDetail2 = new HashMap<>();
        participantDetail2.put("username", "participant2");
        participantDetail2.put("amountPaid", new BigDecimal("0.00"));
        participantDetail2.put("amountOwed", new BigDecimal("50.00"));
        participantDetail2.put("isChecked", true);

        participantDetails.add(participantDetail1);
        participantDetails.add(participantDetail2);

        expectedExpenseDetails.put("participants", participantDetails);
        expectedExpenseDetails.put("gmRemovedDate", gmDetails.getRemovedDate());

        Map<String, Object> actualExpenseDetails = expenseService.getExpenseDetails(expenseId, groupId, username);

        assertEquals(expectedExpenseDetails, actualExpenseDetails);

        verify(theGroupsDAOImpl).findGroupById(groupId);
        verify(expensesDAO).findExpenseById(expenseId);
        verify(expenseParticipantsDAO).findByExpenseId(expenseId);
        verify(groupMembersDAO).getDetails(groupId, username);
    }
}
