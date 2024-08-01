package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.configuration.TestSecurityConfig;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpenseParticipantsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpensesDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.UserDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dto.ExpenseDTO;
import com.PrathihasProjects.PrathihasSplitwise.entity.ExpenseParticipants;
import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import com.PrathihasProjects.PrathihasSplitwise.helper.GroupMembersHelper;
import com.PrathihasProjects.PrathihasSplitwise.jwt.JwtUtil;
import com.PrathihasProjects.PrathihasSplitwise.services.GroupDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddExpenseController.class)
@Import(TestSecurityConfig.class)
public class AddExpenseControllerTest {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDAOImpl theUserDAOImpl;

    @MockBean
    private ExpensesDAOImpl expensesDAO;

    @MockBean
    private ExpenseParticipantsDAOImpl expenseParticipantsDAO;

    @MockBean
    private GroupsDAOImpl theGroupsDAOImpl;

    @MockBean
    private GroupDetailsService groupDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user")
    void addExpense_success () throws Exception {

        int groupId = 1;

        User user = new User();
        user.setUsername("user");

        User payer = new User();
        payer.setUsername("payer");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(theUserDAOImpl.findUserByName("user")).thenReturn(user);

        Groups group = new Groups();
        group.setId(groupId);
        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(group);

        when(theUserDAOImpl.findUserByName("payer")).thenReturn(payer);
        when(theUserDAOImpl.findUserByName("user")).thenReturn(user);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseName("New Expense");
        expenseDTO.setAmount(BigDecimal.valueOf(100.0));
        expenseDTO.setIsPayment(false);

        Map<String, BigDecimal> payers = new HashMap<>();
        payers.put("payer", BigDecimal.valueOf(100.0));
        expenseDTO.setPayers(payers);

        Map<String, Boolean> participants = new HashMap<>();
        participants.put("user", true);
        expenseDTO.setParticipants(participants);

        GroupMembersHelper gmDetails = new GroupMembersHelper("user", groupId, "admin", new Date());
        when(groupDetailsService.getGmDetails(groupId, "user")).thenReturn(gmDetails);

        String requestBody = objectMapper.writeValueAsString(expenseDTO);

        when(expenseParticipantsDAO.findParticipant(anyInt(), anyString())).thenReturn(null);

        mockMvc.perform(post("/splitwise/groups/{groupId}/addExpense", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gmDetails.username").value("user"))
                .andExpect(jsonPath("$.gmDetails.groupId").value(groupId))
                .andExpect(jsonPath("$.gmDetails.addedBy").value("admin"))
                .andExpect(jsonPath("$.gmDetails.addedDate").isNotEmpty());

        verify(theUserDAOImpl, times(2)).findUserByName("user");
        verify(theUserDAOImpl, times(1)).findUserByName("payer");
        verify(theGroupsDAOImpl, times(1)).findGroupById(groupId);
        verify(expensesDAO, times(1)).save(any(Expenses.class));
        verify(expenseParticipantsDAO, times(2)).save(any(ExpenseParticipants.class));
        verify(groupDetailsService, times(1)).getGmDetails(groupId, "user");
    }


    @Test
    @WithMockUser(username = "user")
    void addExpense_successWithExistingParticipant () throws Exception {

        int groupId = 1;

        User user = new User();
        user.setUsername("user");

        User payer = new User();
        payer.setUsername("payer");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(theUserDAOImpl.findUserByName("user")).thenReturn(user);

        Groups group = new Groups();
        group.setId(groupId);
        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(group);

        when(theUserDAOImpl.findUserByName("payer")).thenReturn(payer);
        when(theUserDAOImpl.findUserByName("user")).thenReturn(user);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseName("New Expense");
        expenseDTO.setAmount(BigDecimal.valueOf(100.0));
        expenseDTO.setIsPayment(false);

        Map<String, BigDecimal> payers = new HashMap<>();
        payers.put("payer", BigDecimal.valueOf(100.0));
        expenseDTO.setPayers(payers);

        Map<String, Boolean> participants = new HashMap<>();
        participants.put("user", true);
        expenseDTO.setParticipants(participants);

        GroupMembersHelper gmDetails = new GroupMembersHelper("user", groupId, "admin", new Date());
        when(groupDetailsService.getGmDetails(groupId, "user")).thenReturn(gmDetails);

        String requestBody = objectMapper.writeValueAsString(expenseDTO);

        ExpenseParticipants existingParticipant = new ExpenseParticipants();
        existingParticipant.setAmountOwed(BigDecimal.valueOf(100.0));
        when(expenseParticipantsDAO.findParticipant(anyInt(), eq("user"))).thenReturn(existingParticipant);

        mockMvc.perform(post("/splitwise/groups/{groupId}/addExpense", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gmDetails.username").value("user"))
                .andExpect(jsonPath("$.gmDetails.groupId").value(groupId))
                .andExpect(jsonPath("$.gmDetails.addedBy").value("admin"))
                .andExpect(jsonPath("$.gmDetails.addedDate").isNotEmpty());

        verify(theUserDAOImpl, times(2)).findUserByName("user");
        verify(theUserDAOImpl, times(1)).findUserByName("payer");
        verify(theGroupsDAOImpl, times(1)).findGroupById(groupId);
        verify(expensesDAO, times(1)).save(any(Expenses.class));
        verify(expenseParticipantsDAO, times(1)).save(any(ExpenseParticipants.class));
        verify(expenseParticipantsDAO, times(1)).updateExpenseParticipants(any(ExpenseParticipants.class));
        verify(groupDetailsService, times(1)).getGmDetails(groupId, "user");

    }

    @Test
    @WithMockUser(username = "user")
    void addExpense_InvalidUserInformation () throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        when(theUserDAOImpl.findUserByName("user")).thenReturn(null);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseName("New Expense");
        expenseDTO.setAmount(BigDecimal.valueOf(100.0));
        expenseDTO.setIsPayment(false);

        Map<String, BigDecimal> payers = new HashMap<>();
        payers.put("payer", BigDecimal.valueOf(100.0));
        expenseDTO.setPayers(payers);

        Map<String, Boolean> participants = new HashMap<>();
        participants.put("user", true);
        expenseDTO.setParticipants(participants);

        String requestBody = objectMapper.writeValueAsString(expenseDTO);

        mockMvc.perform(post("/splitwise/groups/{groupId}/addExpense", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid user information."));

        verify(theUserDAOImpl, times(1)).findUserByName("user");
    }

    @Test
    @WithMockUser(username = "user")
    void addExpense_GroupNotFound () throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        when(theUserDAOImpl.findUserByName("user")).thenReturn(new User());

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(null);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseName("New Expense");
        expenseDTO.setAmount(BigDecimal.valueOf(100.0));
        expenseDTO.setIsPayment(false);

        Map<String, BigDecimal> payers = new HashMap<>();
        payers.put("payer", BigDecimal.valueOf(100.0));
        expenseDTO.setPayers(payers);

        Map<String, Boolean> participants = new HashMap<>();
        participants.put("user", true);
        expenseDTO.setParticipants(participants);

        String requestBody = objectMapper.writeValueAsString(expenseDTO);

        mockMvc.perform(post("/splitwise/groups/{groupId}/addExpense", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Group not found"));

        verify(theUserDAOImpl, times(1)).findUserByName("user");
        verify(theGroupsDAOImpl, times(1)).findGroupById(anyInt());
    }

    @Test
    @WithMockUser(username = "user")
    void addExpense_InternalServerError () throws Exception {

        int groupId = 1;

        User user = new User();
        user.setUsername("user");

        User payer = new User();
        payer.setUsername("payer");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(theUserDAOImpl.findUserByName("user")).thenReturn(user);

        Groups group = new Groups();
        group.setId(groupId);
        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(group);

        Expenses expense = new Expenses();
        expense.setId(1);
        expense.setExpenseName("New Expense");
        expense.setAmount(BigDecimal.valueOf(100.0));
        expense.setDateCreated(new Date());
        expense.setAddedBy(user);
        expense.setDeleted(false);
        expense.setPayment(false);

        when(theUserDAOImpl.findUserByName("payer")).thenReturn(payer);
        when(theUserDAOImpl.findUserByName("user")).thenReturn(user);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseName("New Expense");
        expenseDTO.setAmount(BigDecimal.valueOf(100.0));
        expenseDTO.setIsPayment(false);

        Map<String, BigDecimal> payers = new HashMap<>();
        payers.put("payer", BigDecimal.valueOf(100.0));
        expenseDTO.setPayers(payers);

        Map<String, Boolean> participants = new HashMap<>();
        participants.put("user", true);
        expenseDTO.setParticipants(participants);

        String requestBody = objectMapper.writeValueAsString(expenseDTO);

        doThrow(new RuntimeException("Internal server error")).when(expenseParticipantsDAO).save(any(ExpenseParticipants.class));

        when(expenseParticipantsDAO.findParticipant(anyInt(), anyString())).thenReturn(null);

        mockMvc.perform(post("/splitwise/groups/{groupId}/addExpense", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to add expense: Internal server error"));


        verify(theUserDAOImpl, times(1)).findUserByName("user");
        verify(theUserDAOImpl, times(1)).findUserByName("payer");
        verify(theGroupsDAOImpl, times(1)).findGroupById(groupId);
        verify(expensesDAO, times(1)).save(any(Expenses.class));
        verify(expenseParticipantsDAO, times(1)).save(any(ExpenseParticipants.class));
    }

}
