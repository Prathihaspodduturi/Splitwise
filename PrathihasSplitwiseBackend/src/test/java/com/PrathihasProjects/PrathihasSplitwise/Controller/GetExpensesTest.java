package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.configuration.TestSecurityConfig;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpensesDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.helper.GroupMembersHelper;
import com.PrathihasProjects.PrathihasSplitwise.jwt.JwtUtil;
import com.PrathihasProjects.PrathihasSplitwise.services.GroupDetailsService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GetExpenses.class)
@Import(TestSecurityConfig.class)
public class GetExpensesTest {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private GroupsDAOImpl theGroupsDAOImpl;

    @MockBean
    private ExpensesDAOImpl expensesDAO;

    @MockBean
    private GroupDetailsService groupDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user")
    void getExpenses_Success () throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");

        Groups group = new Groups();
        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(group);

        List<Expenses> expenses = Arrays.asList(Mockito.mock(Expenses.class), Mockito.mock(Expenses.class));
        when(expensesDAO.groupExpenses(anyInt())).thenReturn(expenses);

        List<Map<String, Object>> detailedExpenses = new ArrayList<>();
        Map<String, Object> detailedExpense1 = new HashMap<>();
        detailedExpense1.put("id", 1);
        detailedExpense1.put("expenseName", "Dinner Expense");
        detailedExpense1.put("dateCreated", new Date());
        detailedExpense1.put("amount", 100.0);
        detailedExpense1.put("addedBy", "user1");
        detailedExpense1.put("deleted", false);
        detailedExpense1.put("isPayment", false);
        detailedExpense1.put("notInvolved", true);
        detailedExpenses.add(detailedExpense1);

        Map<String, Object> detailedExpense2 = new HashMap<>();
        detailedExpense2.put("id", 2);
        detailedExpense2.put("expenseName", "Movie Expense");
        detailedExpense2.put("dateCreated", new Date());
        detailedExpense2.put("amount", 50.0);
        detailedExpense2.put("addedBy", "user2");
        detailedExpense2.put("deleted", false);
        detailedExpense2.put("isPayment", false);
        detailedExpense2.put("involved", 30.00);
        detailedExpenses.add(detailedExpense2);

        when(groupDetailsService.getDetailedExpenses(anyList(), anyString())).thenReturn(detailedExpenses);

        GroupMembersHelper gmDetails = new GroupMembersHelper("user", 1, "user1", new Date());
        when(groupDetailsService.getGmDetails(anyInt(), anyString())).thenReturn(gmDetails);

        mockMvc.perform(get("/splitwise/groups/{groupId}/expenses", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gmDetails.username", is("user")))
                .andExpect(jsonPath("$.gmDetails.addedBy", is("user1")))
                .andExpect(jsonPath("$.gmDetails.addedDate").exists())
                .andExpect(jsonPath("$.detailedExpenses", hasSize(2)))
                .andExpect(jsonPath("$.detailedExpenses[0].expenseName", is("Dinner Expense")))
                .andExpect(jsonPath("$.detailedExpenses[0].amount", is(100.0)))
                .andExpect(jsonPath("$.detailedExpenses[0].addedBy", is("user1")))
                .andExpect(jsonPath("$.detailedExpenses[0].notInvolved", is(true)))
                .andExpect(jsonPath("$.detailedExpenses[1].expenseName", is("Movie Expense")))
                .andExpect(jsonPath("$.detailedExpenses[1].amount", is(50.0)))
                .andExpect(jsonPath("$.detailedExpenses[1].addedBy", is("user2")))
                .andExpect(jsonPath("$.detailedExpenses[1].involved", is(30.00)));

        verify(theGroupsDAOImpl, times(1)).findGroupById(anyInt());
        verify(expensesDAO, times(1)).groupExpenses(anyInt());
        verify(groupDetailsService, times(1)).getDetailedExpenses(anyList(), anyString());
        verify(groupDetailsService, times(1)).getGmDetails(anyInt(), anyString());

    }


    @Test
    @WithMockUser(username = "user")
    void getExpenses_GroupNotFound () throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(null);

        mockMvc.perform(get("/splitwise/groups/{groupId}/expenses", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Group not found"));

        verify(theGroupsDAOImpl, times(1)).findGroupById(anyInt());

    }

    @Test
    @WithMockUser(username = "user")
    public void testGetExpenses_InternalServerError() throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/splitwise/groups/{groupId}/expenses", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Database error"));

        verify(theGroupsDAOImpl, times(1)).findGroupById(anyInt());
        verify(expensesDAO, times(0)).groupExpenses(anyInt());
        verify(groupDetailsService, times(0)).getDetailedExpenses(anyList(), anyString());
        verify(groupDetailsService, times(0)).getGmDetails(anyInt(), anyString());
    }
}
