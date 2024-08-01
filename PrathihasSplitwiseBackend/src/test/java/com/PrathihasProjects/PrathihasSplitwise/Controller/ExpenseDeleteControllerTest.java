package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.configuration.TestSecurityConfig;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpenseParticipantsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpensesDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.UserDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.ExpenseParticipants;
import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import com.PrathihasProjects.PrathihasSplitwise.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseDeleteController.class)
@Import(TestSecurityConfig.class)
public class ExpenseDeleteControllerTest {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private ExpensesDAOImpl expensesDAO;

    @MockBean
    private ExpenseParticipantsDAOImpl expenseParticipantsDAO;

    @MockBean
    private UserDAOImpl theUserDAOImpl;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user")
    void deleteExpense_success () throws Exception {
        int groupId = 1;
        int expenseId = 1;

        Expenses expense = new Expenses();
        expense.setDeleted(false);
        expense.setId(expenseId);

        when(expensesDAO.findExpenseById(anyInt())).thenReturn(expense);

        User user = new User();
        user.setUsername("user");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        when(theUserDAOImpl.findUserByName(anyString())).thenReturn(user);

        List<ExpenseParticipants> participantsList = new ArrayList<>();
        ExpenseParticipants participant = new ExpenseParticipants();
        participant.setDeleted(false);
        participantsList.add(participant);

        when(expenseParticipantsDAO.findByExpenseId(anyInt())).thenReturn(participantsList);

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/delete", groupId, expenseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Expense deleted successfully"));

        verify(expensesDAO, times(1)).findExpenseById(anyInt());
        verify(theUserDAOImpl, times(2)).findUserByName(anyString());
        verify(expensesDAO).updateExpense(expense);
        verify(expenseParticipantsDAO, times(1)).findByExpenseId(anyInt());
    }

    @Test
    @WithMockUser(username = "user")
    void deleteExpense_expenseNotFound () throws Exception {

        when(expensesDAO.findExpenseById(anyInt())).thenReturn(null);

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/delete", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Expense not found"));

        verify(expensesDAO, times(1)).findExpenseById(anyInt());
    }

    @Test
    @WithMockUser(username = "user")
    void deleteExpense_userNotFound () throws Exception {
        int expenseId = 1;

        Expenses expense = new Expenses();
        expense.setDeleted(false);
        expense.setId(expenseId);

        when(expensesDAO.findExpenseById(anyInt())).thenReturn(expense);

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        when(theUserDAOImpl.findUserByName(anyString())).thenReturn(null);

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/delete", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid user information."));

        verify(expensesDAO, times(1)).findExpenseById(anyInt());
        verify(theUserDAOImpl, times(1)).findUserByName(anyString());
    }

    @Test
    @WithMockUser(username = "user")
    void deleteExpense_InternalServerError () throws Exception {
        int expenseId = 1;

        Expenses expense = new Expenses();
        expense.setDeleted(false);
        expense.setId(expenseId);

        when(expensesDAO.findExpenseById(anyInt())).thenReturn(expense);

        User user = new User();
        user.setUsername("user");

        when(theUserDAOImpl.findUserByName(anyString())).thenReturn(user);

        doThrow(new RuntimeException("Internal server error")).when(expensesDAO).updateExpense(any(Expenses.class));

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/delete", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to delete expense: Internal server error"));

        verify(expensesDAO, times(1)).findExpenseById(anyInt());
        verify(theUserDAOImpl, times(2)).findUserByName(anyString());
        verify(expensesDAO).updateExpense(expense);
    }
}
