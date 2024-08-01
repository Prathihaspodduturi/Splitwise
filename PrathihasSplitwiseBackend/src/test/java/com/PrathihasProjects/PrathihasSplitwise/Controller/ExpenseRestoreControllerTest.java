package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.configuration.TestSecurityConfig;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpenseParticipantsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpensesDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.ExpenseParticipants;
import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;
import com.PrathihasProjects.PrathihasSplitwise.jwt.JwtUtil;
import com.PrathihasProjects.PrathihasSplitwise.services.ExpenseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseRestoreController.class)
@Import(TestSecurityConfig.class)
public class ExpenseRestoreControllerTest {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private ExpensesDAOImpl expensesDAO;

    @MockBean
    private ExpenseParticipantsDAOImpl expenseParticipantsDAO;

    @MockBean
    private ExpenseService expenseService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser(username = "user")
    void restoreExpense () throws Exception {

        String username = "user";
        int groupId = 1;
        int expenseId = 1;

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        Expenses expense = new Expenses();
        expense.setId(expenseId);
        expense.setDeleted(true);

        when(expensesDAO.findExpenseById(expenseId)).thenReturn(expense);

        List<ExpenseParticipants> participantsList = new ArrayList<>();
        ExpenseParticipants participant = new ExpenseParticipants();
        participant.setDeleted(true);
        participantsList.add(participant);

        when(expenseParticipantsDAO.findByExpenseId(expenseId)).thenReturn(participantsList);

        Map<String, Object> expenseDetails = new HashMap<>();
        expenseDetails.put("expenseName", "Test Expense");
        expenseDetails.put("amount", 100.0);
        expenseDetails.put("dateCreated", new Date());
        expenseDetails.put("addedBy", "user");
        expenseDetails.put("isPayment", false);
        expenseDetails.put("isDeleted", false);

        when(expenseService.getExpenseDetails(expenseId, groupId, username)).thenReturn(expenseDetails);

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/restore", groupId, expenseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseName", is("Test Expense")))
                .andExpect(jsonPath("$.amount", is(100.0)))
                .andExpect(jsonPath("$.addedBy", is("user")))
                .andExpect(jsonPath("$.isPayment", is(false)))
                .andExpect(jsonPath("$.isDeleted", is(false)));

        verify(expensesDAO, times(1)).findExpenseById(expenseId);
        verify(expensesDAO).updateExpense(expense);
        verify(expenseParticipantsDAO, times(1)).findByExpenseId(expenseId);
        verify(expenseParticipantsDAO, times(1)).updateExpenseParticipants(participantsList.get(0));
        verify(expenseService, times(1)).getExpenseDetails(expenseId, groupId, username);

    }

    @Test
    @WithMockUser(username = "user")
    public void testRestoreExpense_ExpenseNotFound() throws Exception {
        String username = "user";
        int groupId = 1;
        int expenseId = 1;

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(expensesDAO.findExpenseById(expenseId)).thenReturn(null);

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/restore", groupId, expenseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Expense not found"));

        verify(expensesDAO, times(1)).findExpenseById(expenseId);
        verify(expenseParticipantsDAO, times(0)).findByExpenseId(expenseId);
        verify(expenseService, times(0)).getExpenseDetails(expenseId, groupId, username);
    }

    @Test
    @WithMockUser(username = "user")
    public void testRestoreExpense_noExpenseDetailsFound() throws Exception {
        String username = "user";
        int groupId = 1;
        int expenseId = 1;

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        Expenses expense = new Expenses();
        expense.setId(expenseId);
        expense.setDeleted(true);

        when(expensesDAO.findExpenseById(expenseId)).thenReturn(expense);

        when(expenseService.getExpenseDetails(anyInt(), anyInt(), anyString())).thenReturn(null);

        List<ExpenseParticipants> participantsList = new ArrayList<>();
        ExpenseParticipants participant = new ExpenseParticipants();
        participant.setDeleted(true);
        participantsList.add(participant);

        when(expenseParticipantsDAO.findByExpenseId(expenseId)).thenReturn(participantsList);

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/restore", groupId, expenseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Expense not found or has been deleted"));

        verify(expensesDAO, times(1)).findExpenseById(expenseId);
        verify(expenseParticipantsDAO, times(1)).findByExpenseId(expenseId);
        verify(expenseService, times(1)).getExpenseDetails(expenseId, groupId, username);
    }

    @Test
    @WithMockUser(username = "user")
    public void testRestoreExpense_InternalServerError () throws Exception {
        String username = "user";
        int groupId = 1;
        int expenseId = 1;

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        when(expensesDAO.findExpenseById(anyInt())).thenThrow(new RuntimeException("Internal problem"));

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/restore", groupId, expenseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to restore expense: Internal problem"));

        verify(expensesDAO, times(1)).findExpenseById(expenseId);
    }
}
