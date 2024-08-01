package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.configuration.TestSecurityConfig;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpensesDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.jwt.JwtUtil;
import com.PrathihasProjects.PrathihasSplitwise.services.ExpenseService;
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

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GetExpenseDetailsController.class)
@Import(TestSecurityConfig.class)
public class GetExpenseDetailsControllerTest {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private GroupsDAOImpl theGroupsDAOImpl;

    @MockBean
    private ExpensesDAOImpl expensesDAO;

    @MockBean
    private ExpenseService expenseService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user")
    void getExpenseDetails_success () throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(new Groups());

        when(expensesDAO.findExpenseById(anyInt())).thenReturn(new Expenses());

        Map<String,Object> expenseDetails = new HashMap<>();

        expenseDetails.put("expenseName", "Test Expense");
        expenseDetails.put("amount", 100.0);
        expenseDetails.put("dateCreated", new Date());
        expenseDetails.put("addedBy", "user1");
        expenseDetails.put("isPayment", false);
        expenseDetails.put("isDeleted", false);

        List<Map<String, Object>> participants = new ArrayList<>();
        Map<String, Object> participantDetails1 = new HashMap<>();
        participantDetails1.put("username", "participant1");
        participantDetails1.put("amountPaid", new BigDecimal("50.0"));
        participants.add(participantDetails1);

        Map<String, Object> participantDetails2 = new HashMap<>();
        participantDetails2.put("username", "participant2");
        participantDetails2.put("amountOwed", new BigDecimal("50.0"));
        participants.add(participantDetails2);

        expenseDetails.put("participants", participants);
        when(expenseService.getExpenseDetails(anyInt(), anyInt(), anyString())).thenReturn(expenseDetails);

        mockMvc.perform(get("/splitwise/groups/{groupId}/expenses/{expenseId}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseName", is("Test Expense")))
                .andExpect(jsonPath("$.amount", is(100.0)))
                .andExpect(jsonPath("$.addedBy", is("user1")))
                .andExpect(jsonPath("$.isPayment", is(false)))
                .andExpect(jsonPath("$.isDeleted", is(false)))
                .andExpect(jsonPath("$.dateCreated").exists())
                .andExpect(jsonPath("$.participants", hasSize(2)))
                .andExpect(jsonPath("$.participants[0].username", is("participant1")))
                .andExpect(jsonPath("$.participants[0].amountPaid", is(50.0)))
                .andExpect(jsonPath("$.participants[1].username", is("participant2")))
                .andExpect(jsonPath("$.participants[1].amountOwed", is(50.0)));
    }


    @Test
    @WithMockUser(username = "user")
    void getExpenseDetails_GroupNotFound () throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(null);

        mockMvc.perform(get("/splitwise/groups/{groupId}/expenses/{expenseId}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Group not found"));

        verify(theGroupsDAOImpl).findGroupById(anyInt());

    }

    @Test
    @WithMockUser(username = "user")
    void getExpenseDetails_ExpenseNotFound () throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(new Groups());
        when(expensesDAO.findExpenseById(anyInt())).thenReturn(null);

        mockMvc.perform(get("/splitwise/groups/{groupId}/expenses/{expenseId}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Expense not found or has been deleted"));

        verify(theGroupsDAOImpl).findGroupById(anyInt());
        verify(expensesDAO).findExpenseById(anyInt());

    }

    @Test
    @WithMockUser(username = "user")
    void getExpenseDetails_NoExpenseDetailsFound () throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(new Groups());
        when(expensesDAO.findExpenseById(anyInt())).thenReturn(new Expenses());
        when(expenseService.getExpenseDetails(anyInt(), anyInt(), anyString())).thenReturn(null);

        mockMvc.perform(get("/splitwise/groups/{groupId}/expenses/{expenseId}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Expense not found or has been deleted"));

        verify(theGroupsDAOImpl).findGroupById(anyInt());
        verify(expensesDAO).findExpenseById(anyInt());
        verify(expenseService).getExpenseDetails(anyInt(), anyInt(), anyString());

    }

    @Test
    @WithMockUser(username = "user")
    void getExpenseDetails_InternalServerError () throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenThrow(new RuntimeException("Internal error"));

        mockMvc.perform(get("/splitwise/groups/{groupId}/expenses/{expenseId}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to retrieve expense details: Internal error"));

        verify(theGroupsDAOImpl).findGroupById(anyInt());
    }
}
