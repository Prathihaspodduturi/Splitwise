package com.PrathihasProjects.PrathihasSplitwise.services;

import com.PrathihasProjects.PrathihasSplitwise.dao.ExpenseParticipantsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.*;
import com.PrathihasProjects.PrathihasSplitwise.helper.GroupMembersHelper;
import com.PrathihasProjects.PrathihasSplitwise.helper.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GroupDetailsServiceTest {

    AutoCloseable openMocks;

    @Mock
    private ExpenseParticipantsDAOImpl expenseParticipantsDAO;

    @Mock
    private GroupMembersDAOImpl groupMembersDAO;

    @InjectMocks
    private GroupDetailsService groupDetailsService;

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
    void getGmDetailsTest () {

        int groupId = 1;
        String username = "user";

        GroupMembers gmGroupMembers = new GroupMembers();
        User user = new User();
        user.setUsername("user");

        gmGroupMembers.setUser(user);

        Groups group = new Groups();
        group.setId(groupId);

        User addedBy = new User();
        addedBy.setUsername("addedBy");

        gmGroupMembers.setAddedBy(addedBy);
        gmGroupMembers.setGroup(group);
        gmGroupMembers.setAddedDate(new Date());

        when(groupMembersDAO.getDetails(groupId, username)).thenReturn(gmGroupMembers);

        GroupMembersHelper gmDetails = new GroupMembersHelper(gmGroupMembers.getUser().getUsername(), gmGroupMembers.getGroup().getId(), gmGroupMembers.getAddedBy().getUsername(), gmGroupMembers.getAddedDate());

        GroupMembersHelper result = groupDetailsService.getGmDetails(groupId, username);

        assertAll(
                () -> assertEquals(gmDetails.getUsername(), result.getUsername()),
                () -> assertEquals(gmDetails.getGroupId(), result.getGroupId()),
                () -> assertEquals(gmDetails.getAddedBy(), result.getAddedBy()),
                () -> assertEquals(gmDetails.getAddedDate(), result.getAddedDate()),
                () -> assertEquals(gmDetails.getRemovedBy(), result.getRemovedBy()),
                () -> assertEquals(gmDetails.getRemovedDate(), result.getRemovedDate())
        );

        verify(groupMembersDAO).getDetails(groupId, username);

    }

    @Test
    void getGmDetailsTest_hasRemovedBy() {

        int groupId = 1;
        String username = "user";

        GroupMembers gmGroupMembers = new GroupMembers();
        User user = new User();
        user.setUsername("user");

        gmGroupMembers.setUser(user);

        Groups group = new Groups();
        group.setId(groupId);

        User addedBy = new User();
        addedBy.setUsername("addedBy");

        User removedBy = new User();
        addedBy.setUsername("removedBy");

        gmGroupMembers.setAddedBy(addedBy);
        gmGroupMembers.setGroup(group);
        gmGroupMembers.setAddedDate(new Date());
        gmGroupMembers.setRemovedBy(removedBy);
        gmGroupMembers.setRemovedDate(new Date());

        when(groupMembersDAO.getDetails(groupId, username)).thenReturn(gmGroupMembers);

        GroupMembersHelper gmDetails = new GroupMembersHelper(gmGroupMembers.getUser().getUsername(), gmGroupMembers.getGroup().getId(), gmGroupMembers.getAddedBy().getUsername(), gmGroupMembers.getAddedDate());
        gmDetails.setRemovedBy(gmGroupMembers.getRemovedBy().getUsername());
        gmDetails.setRemovedDate(gmGroupMembers.getRemovedDate());

        GroupMembersHelper result = groupDetailsService.getGmDetails(groupId, username);

        assertAll(
                () -> assertEquals(gmDetails.getUsername(), result.getUsername()),
                () -> assertEquals(gmDetails.getGroupId(), result.getGroupId()),
                () -> assertEquals(gmDetails.getAddedBy(), result.getAddedBy()),
                () -> assertEquals(gmDetails.getAddedDate(), result.getAddedDate()),
                () -> assertEquals(gmDetails.getRemovedBy(), result.getRemovedBy()),
                () -> assertEquals(gmDetails.getRemovedDate(), result.getRemovedDate())
        );

        verify(groupMembersDAO).getDetails(groupId, username);

    }


    @Test
    void testGetDetailedExpenses_ParticipantInvolvedAndNotInvolved () {

        String username ="testuser";

        Expenses expense = new Expenses();
        expense.setId(1);
        expense.setExpenseName("Test Expense");
        expense.setDateCreated(new Date());
        expense.setAmount(new BigDecimal("100.00"));
        User addedBy = new User();
        addedBy.setUsername("user1");
        expense.setAddedBy(addedBy);
        expense.setDeleted(false);
        expense.setPayment(false);

        ExpenseParticipants participant = new ExpenseParticipants();
        participant.setAmountpaid(new BigDecimal("50.00"));
        participant.setAmountOwed(new BigDecimal("25.00"));

        when(expenseParticipantsDAO.findParticipant(expense.getId(), username)).thenReturn(participant);

        List<Expenses> expensesList = Collections.singletonList(expense);
        List<Map<String, Object>> detailedExpenses1 = groupDetailsService.getDetailedExpenses(expensesList, username);


        //assertEquals(1, detailedExpenses.size());

        Map<String, Object> expenseDetails = detailedExpenses1.get(0);

        assertEquals("Test Expense", expenseDetails.get("expenseName"));
        assertEquals(new BigDecimal("100.00"), expenseDetails.get("amount"));
        assertEquals("user1", expenseDetails.get("addedBy"));
        assertEquals(false, expenseDetails.get("deleted"));
        assertEquals(false, expenseDetails.get("isPayment"));
        assertEquals(new BigDecimal("25.00"), expenseDetails.get("involved"));




        // not involved participant

        String username2 = "testuser2";

        when(expenseParticipantsDAO.findParticipant(expense.getId(), username2)).thenReturn(null);

        List<Map<String,Object>> detailedExpenses2 = groupDetailsService.getDetailedExpenses(expensesList, username2);
        Map<String,Object> expenseDetails2 = detailedExpenses2.get(0);
        assertEquals(true, expenseDetails2.get("notInvolved"));

        String username3 = "testuser3";
        // involved but Amount Owed and amount paid is zero due to edit in the expense.
        ExpenseParticipants participant3 = new ExpenseParticipants();
        participant3.setAmountpaid(new BigDecimal("0.00"));
        participant3.setAmountOwed(new BigDecimal("0.00"));

        when(expenseParticipantsDAO.findParticipant(expense.getId(), username3)).thenReturn(participant3);

        List<Map<String,Object>> detailedExpenses3 = groupDetailsService.getDetailedExpenses(expensesList, username3);
        Map<String,Object> expenseDetails3 = detailedExpenses3.get(0);
        assertEquals(true, expenseDetails3.get("notInvolved"));

    }


    @Test
    void testGetDetailedExpenses_UpdatedByAndDeletedByIsNotNull () {

        String username ="testuser";

        Expenses expense = new Expenses();
        expense.setId(1);
        expense.setExpenseName("Test Expense");
        expense.setDateCreated(new Date());
        expense.setAmount(new BigDecimal("100.00"));
        User addedBy = new User();
        addedBy.setUsername("user1");
        expense.setAddedBy(addedBy);
        User updatedBy = new User();
        updatedBy.setUsername("user2");
        expense.setUpdatedBy(updatedBy);
        expense.setLastUpdatedDate(new Date());
        User deletedBy = new User();
        deletedBy.setUsername("user3");
        expense.setDeletedBy(deletedBy);
        expense.setDeletedDate(new Date());
        expense.setDeleted(true);
        expense.setPayment(false);

        ExpenseParticipants participant = new ExpenseParticipants();
        participant.setAmountpaid(new BigDecimal("50.00"));
        participant.setAmountOwed(new BigDecimal("25.00"));

        when(expenseParticipantsDAO.findParticipant(expense.getId(), username)).thenReturn(participant);

        List<Expenses> expensesList = Collections.singletonList(expense);
        List<Map<String, Object>> detailedExpenses1 = groupDetailsService.getDetailedExpenses(expensesList, username);


        Map<String, Object> expenseDetails = detailedExpenses1.get(0);

        assertEquals("Test Expense", expenseDetails.get("expenseName"));
        assertEquals(new BigDecimal("100.00"), expenseDetails.get("amount"));
        assertEquals("user1", expenseDetails.get("addedBy"));
        assertEquals(true, expenseDetails.get("deleted"));
        assertEquals(false, expenseDetails.get("isPayment"));
        assertEquals(new BigDecimal("25.00"), expenseDetails.get("involved"));
        assertEquals("user2", expenseDetails.get("updatedBy"));
        assertEquals("user3", expenseDetails.get("deletedBy"));
        assertEquals(expense.getLastUpdatedDate(), expenseDetails.get("lastUpdatedDate"));
        assertEquals(expense.getDeletedDate(), expenseDetails.get("deletedDate"));
    }


    @Test
    public void testGetAllTransactions() {
        Expenses expense1 = new Expenses();
        expense1.setId(1);
        expense1.setDeleted(false);

        Expenses expense2 = new Expenses();
        expense2.setId(2);
        expense2.setDeleted(false);

        List<Expenses> expensesList = Arrays.asList(expense1, expense2);

        ExpenseParticipants participant1 = new ExpenseParticipants();
        User user1 = new User();
        user1.setUsername("user1");
        participant1.setUser(user1);
        participant1.setAmountpaid(new BigDecimal("100.00"));
        participant1.setAmountOwed(new BigDecimal("50.00"));
        participant1.setDeleted(false);

        ExpenseParticipants participant2 = new ExpenseParticipants();
        User user2 = new User();
        user2.setUsername("user2");
        participant2.setUser(user2);
        participant2.setAmountpaid(new BigDecimal("50.00"));
        participant2.setAmountOwed(new BigDecimal("100.00"));
        participant2.setDeleted(false);

        when(expenseParticipantsDAO.findByExpenseId(1)).thenReturn(Collections.singletonList(participant1));
        when(expenseParticipantsDAO.findByExpenseId(2)).thenReturn(Collections.singletonList(participant2));

        List<Transaction> transactions = groupDetailsService.getAllTransactions(expensesList);

        assertEquals(1, transactions.size());
        Transaction transaction = transactions.get(0);
        assertEquals("user2", transaction.getFromUser());
        assertEquals("user1", transaction.getToUser());
        assertEquals(new BigDecimal("50.00"), transaction.getAmount());
    }

    @Test
    public void testGetAllTransactions_NoParticipants() {
        Expenses expense1 = new Expenses();
        expense1.setId(1);
        expense1.setDeleted(false);

        List<Expenses> expensesList = Collections.singletonList(expense1);

        when(expenseParticipantsDAO.findByExpenseId(1)).thenReturn(Collections.emptyList());

        List<Transaction> transactions = groupDetailsService.getAllTransactions(expensesList);

        assertEquals(0, transactions.size());
    }

    @Test
    public void testGetAllTransactions_DeletedExpenses() {
        Expenses expense1 = new Expenses();
        expense1.setId(1);
        expense1.setDeleted(true);

        List<Expenses> expensesList = Collections.singletonList(expense1);

        List<Transaction> transactions = groupDetailsService.getAllTransactions(expensesList);

        assertEquals(0, transactions.size());
    }

    @Test
    public void testGetAllTransactions_ParticipantDeleted() {
        Expenses expense1 = new Expenses();
        expense1.setId(1);
        expense1.setDeleted(false);

        List<Expenses> expensesList = Collections.singletonList(expense1);

        ExpenseParticipants participant1 = new ExpenseParticipants();
        User user1 = new User();
        user1.setUsername("user1");
        participant1.setUser(user1);
        participant1.setAmountpaid(new BigDecimal("100.00"));
        participant1.setAmountOwed(new BigDecimal("50.00"));
        participant1.setDeleted(true);

        when(expenseParticipantsDAO.findByExpenseId(1)).thenReturn(Collections.singletonList(participant1));

        List<Transaction> transactions = groupDetailsService.getAllTransactions(expensesList);

        assertEquals(0, transactions.size());
    }
}
