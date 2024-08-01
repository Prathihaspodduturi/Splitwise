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
import com.PrathihasProjects.PrathihasSplitwise.jwt.JwtUtil;
import com.PrathihasProjects.PrathihasSplitwise.services.ExpenseService;
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
import java.util.*;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseUpdateController.class)
@Import(TestSecurityConfig.class)
public class ExpenseUpdateControllerTest {

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
    private ExpenseService expenseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user")
    void updateExpense_success () throws Exception {
        int groupId = 1;
        int expenseId = 1;

        User user = new User();
        user.setUsername("user");

        User payer = new User();
        payer.setUsername("payer");

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");
        when(theUserDAOImpl.findUserByName("user")).thenReturn(user);

        when(theGroupsDAOImpl.findGroupById(groupId)).thenReturn(new Groups());

        Expenses expense = new Expenses();
        expense.setId(expenseId);
        expense.setExpenseName("New Expense");
        expense.setAmount(BigDecimal.valueOf(100.0));
        expense.setDateCreated(new Date());
        expense.setAddedBy(user);
        expense.setDeleted(false);
        expense.setPayment(false);

        when(expensesDAO.findExpenseById(expenseId)).thenReturn(expense);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseName("Updated Expense");
        expenseDTO.setAmount(BigDecimal.valueOf(150.0));
        expenseDTO.setIsPayment(false);

        Map<String, BigDecimal> payers = new HashMap<>();
        payers.put("payer", BigDecimal.valueOf(150.0));
        expenseDTO.setPayers(payers);

        Map<String, Boolean> participants = new HashMap<>();
        participants.put("user", true);
        expenseDTO.setParticipants(participants);

        when(expenseParticipantsDAO.findParticipant(expenseId, "payer")).thenReturn(null);
        when(theUserDAOImpl.findUserByName("payer")).thenReturn(payer);

        when(expenseParticipantsDAO.findParticipant(expenseId, "user")).thenReturn(null);
        when(theUserDAOImpl.findUserByName("user")).thenReturn(user);


        String requestBody = objectMapper.writeValueAsString(expenseDTO);

        Map<String, Object> expenseDetails = new HashMap<>();
        expenseDetails.put("expenseName", "Updated Expense");
        expenseDetails.put("amount", 150.0);
        expenseDetails.put("updatedBy", "user");
        expenseDetails.put("isPayment", false);
        expenseDetails.put("lastUpdatedDate", new Date());

        Map<String,Object> participantDetails1 = new HashMap<>();
        participantDetails1.put("username", "payer");
        participantDetails1.put("amountPaid", 150.0);
        participantDetails1.put("amountOwed", 0);

        Map<String,Object> participantDetails2 = new HashMap<>();
        participantDetails2.put("username", "user");
        participantDetails2.put("amountPaid", 0);
        participantDetails2.put("amountOwed", 150);

        List<Map<String, Object>> participantList = new ArrayList<>();
        participantList.add(participantDetails1);
        participantList.add(participantDetails2);

        expenseDetails.put("participants", participantList);

        when(expenseService.getExpenseDetails(expenseId, groupId, "user")).thenReturn(expenseDetails);

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/update", groupId, expenseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseName").value("Updated Expense"))
                .andExpect(jsonPath("$.amount").value(150.0))
                .andExpect(jsonPath("$.lastUpdatedDate").exists())
                .andExpect(jsonPath("$.updatedBy").exists())
                .andExpect(jsonPath("$.isPayment").value(false))
                .andExpect(jsonPath("$.participants[0].username").value("payer"))
                .andExpect(jsonPath("$.participants[0].amountPaid").value(150.0))
                .andExpect(jsonPath("$.participants[0].amountOwed").value(0))
                .andExpect(jsonPath("$.participants[1].username").value("user"))
                .andExpect(jsonPath("$.participants[1].amountPaid").value(0))
                .andExpect(jsonPath("$.participants[1].amountOwed").value(150.0));


        verify(theUserDAOImpl, times(2)).findUserByName("user");
        verify(theUserDAOImpl, times(1)).findUserByName("payer");
        verify(theGroupsDAOImpl, times(1)).findGroupById(groupId);
        verify(expensesDAO, times(1)).updateExpense(any(Expenses.class));
        verify(expenseParticipantsDAO, times(2)).save(any(ExpenseParticipants.class));
        verify(expenseService, times(1)).getExpenseDetails(anyInt(), anyInt(), anyString());
    }


    @Test
    @WithMockUser(username = "user")
    void updateExpense_success_withExistingParticipants() throws Exception {
        int groupId = 1;
        int expenseId = 1;

        User user = new User();
        user.setUsername("user");

        User payer = new User();
        payer.setUsername("payer");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(theUserDAOImpl.findUserByName("user")).thenReturn(user);

        Groups group = new Groups();
        when(theGroupsDAOImpl.findGroupById(groupId)).thenReturn(group);

        Expenses expense = new Expenses();
        expense.setId(expenseId);
        expense.setExpenseName("New Expense");
        expense.setAmount(BigDecimal.valueOf(100.0));
        expense.setDateCreated(new Date());
        expense.setAddedBy(user);
        expense.setDeleted(false);
        expense.setPayment(false);

        when(expensesDAO.findExpenseById(expenseId)).thenReturn(expense);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseName("Updated Expense");
        expenseDTO.setAmount(BigDecimal.valueOf(150.0));
        expenseDTO.setIsPayment(false);

        Map<String, BigDecimal> payers = new HashMap<>();
        payers.put("payer", BigDecimal.valueOf(150.0));
        expenseDTO.setPayers(payers);

        Map<String, Boolean> participants = new HashMap<>();
        participants.put("user", true);
        expenseDTO.setParticipants(participants);

        ExpenseParticipants existingPayer = new ExpenseParticipants(expense, payer, BigDecimal.ZERO, BigDecimal.valueOf(100.0));
        ExpenseParticipants existingParticipant = new ExpenseParticipants(expense, user, BigDecimal.valueOf(50.0), BigDecimal.ZERO);

        when(expenseParticipantsDAO.findParticipant(expenseId, "payer")).thenReturn(existingPayer);
        when(expenseParticipantsDAO.findParticipant(expenseId, "user")).thenReturn(existingParticipant);

        String requestBody = objectMapper.writeValueAsString(expenseDTO);

        Map<String, Object> expenseDetails = new HashMap<>();
        expenseDetails.put("expenseName", "Updated Expense");
        expenseDetails.put("amount", 150.0);
        expenseDetails.put("updatedBy", "user");
        expenseDetails.put("isPayment", false);
        expenseDetails.put("lastUpdatedDate", new Date());

        Map<String,Object> participantDetails1 = new HashMap<>();
        participantDetails1.put("username", "payer");
        participantDetails1.put("amountPaid", 150.0);
        participantDetails1.put("amountOwed", 0);

        Map<String,Object> participantDetails2 = new HashMap<>();
        participantDetails2.put("username", "user");
        participantDetails2.put("amountPaid", 0);
        participantDetails2.put("amountOwed", 150);

        List<Map<String, Object>> participantList = new ArrayList<>();
        participantList.add(participantDetails1);
        participantList.add(participantDetails2);

        expenseDetails.put("participants", participantList);

        when(expenseService.getExpenseDetails(expenseId, groupId, "user")).thenReturn(expenseDetails);

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/update", groupId, expenseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseName").value("Updated Expense"))
                .andExpect(jsonPath("$.amount").value(150.0))
                .andExpect(jsonPath("$.lastUpdatedDate").exists())
                .andExpect(jsonPath("$.updatedBy").exists())
                .andExpect(jsonPath("$.isPayment").value(false))
                .andExpect(jsonPath("$.participants[0].username").value("payer"))
                .andExpect(jsonPath("$.participants[0].amountPaid").value(150.0))
                .andExpect(jsonPath("$.participants[0].amountOwed").value(0))
                .andExpect(jsonPath("$.participants[1].username").value("user"))
                .andExpect(jsonPath("$.participants[1].amountPaid").value(0))
                .andExpect(jsonPath("$.participants[1].amountOwed").value(150.0));

        verify(theUserDAOImpl, times(1)).findUserByName("user");
        verify(theGroupsDAOImpl, times(1)).findGroupById(groupId);
        verify(expensesDAO, times(1)).updateExpense(any(Expenses.class));
        verify(expenseParticipantsDAO, times(2)).updateExpenseParticipants(any(ExpenseParticipants.class));
        verify(expenseService, times(1)).getExpenseDetails(anyInt(), anyInt(), anyString());
    }

    @Test
    @WithMockUser(username = "user")
    void updateExpense_success_withParticipantBeingRemovedFromTheExpense () throws Exception {
        int groupId = 1;
        int expenseId = 1;

        User user = new User();
        user.setUsername("user");

        User payer = new User();
        payer.setUsername("payer");

        User toBeRemoved = new User();
        toBeRemoved.setUsername("toBeRemoved");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");
        when(theUserDAOImpl.findUserByName("user")).thenReturn(user);

        Groups group = new Groups();
        when(theGroupsDAOImpl.findGroupById(groupId)).thenReturn(group);

        Expenses expense = new Expenses();
        expense.setId(expenseId);
        expense.setExpenseName("New Expense");
        expense.setAmount(BigDecimal.valueOf(100.0));
        expense.setDateCreated(new Date());
        expense.setAddedBy(user);
        expense.setDeleted(false);
        expense.setPayment(false);

        when(expensesDAO.findExpenseById(expenseId)).thenReturn(expense);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseName("Updated Expense");
        expenseDTO.setAmount(BigDecimal.valueOf(150.0));
        expenseDTO.setIsPayment(false);

        Map<String, BigDecimal> payers = new HashMap<>();
        payers.put("payer", BigDecimal.valueOf(150.0));
        expenseDTO.setPayers(payers);

        Map<String, Boolean> participants = new HashMap<>();
        participants.put("user", true);
        participants.put("toBeRemoved", false);
        expenseDTO.setParticipants(participants);

        when(expenseParticipantsDAO.findParticipant(expenseId, "payer")).thenReturn(null);
        when(theUserDAOImpl.findUserByName("payer")).thenReturn(payer);

        when(expenseParticipantsDAO.findParticipant(expenseId, "user")).thenReturn(null);
        when(theUserDAOImpl.findUserByName("user")).thenReturn(user);

        ExpenseParticipants toBeRemovedParticipant = new ExpenseParticipants(expense, toBeRemoved, BigDecimal.ZERO, BigDecimal.ZERO);

        when(expenseParticipantsDAO.findParticipant(expenseId, "toBeRemoved")).thenReturn(toBeRemovedParticipant);

        String requestBody = objectMapper.writeValueAsString(expenseDTO);

        Map<String, Object> expenseDetails = new HashMap<>();
        expenseDetails.put("expenseName", "Updated Expense");
        expenseDetails.put("amount", 150.0);
        expenseDetails.put("updatedBy", "user");
        expenseDetails.put("isPayment", false);
        expenseDetails.put("lastUpdatedDate", new Date());

        Map<String,Object> participantDetails1 = new HashMap<>();
        participantDetails1.put("username", "payer");
        participantDetails1.put("amountPaid", 150.0);
        participantDetails1.put("amountOwed", 0);

        Map<String,Object> participantDetails2 = new HashMap<>();
        participantDetails2.put("username", "user");
        participantDetails2.put("amountPaid", 0);
        participantDetails2.put("amountOwed", 150);

        List<Map<String, Object>> participantList = new ArrayList<>();
        participantList.add(participantDetails1);
        participantList.add(participantDetails2);

        expenseDetails.put("participants", participantList);

        when(expenseService.getExpenseDetails(expenseId, groupId, "user")).thenReturn(expenseDetails);

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/update", groupId, expenseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseName").value("Updated Expense"))
                .andExpect(jsonPath("$.amount").value(150.0))
                .andExpect(jsonPath("$.lastUpdatedDate").exists())
                .andExpect(jsonPath("$.updatedBy").exists())
                .andExpect(jsonPath("$.isPayment").value(false))
                .andExpect(jsonPath("$.participants[0].username").value("payer"))
                .andExpect(jsonPath("$.participants[0].amountPaid").value(150.0))
                .andExpect(jsonPath("$.participants[0].amountOwed").value(0))
                .andExpect(jsonPath("$.participants[1].username").value("user"))
                .andExpect(jsonPath("$.participants[1].amountPaid").value(0))
                .andExpect(jsonPath("$.participants[1].amountOwed").value(150.0));

        verify(theUserDAOImpl, times(2)).findUserByName("user");
        verify(theUserDAOImpl, times(1)).findUserByName("payer");
        verify(theGroupsDAOImpl, times(1)).findGroupById(groupId);
        verify(expensesDAO, times(1)).updateExpense(any(Expenses.class));
        verify(expenseParticipantsDAO, times(2)).save(any(ExpenseParticipants.class));
        verify(expenseParticipantsDAO, times(1)).deleteParticipantByExpenseAndUser(expenseId, "toBeRemoved");
        verify(expenseService, times(1)).getExpenseDetails(anyInt(), anyInt(), anyString());
    }

    @Test
    @WithMockUser(username = "user")
    void updateExpense_userNotFound () throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");

        when(theUserDAOImpl.findUserByName(anyString())).thenReturn(null);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseName("Updated Expense");
        expenseDTO.setAmount(BigDecimal.valueOf(150.0));
        expenseDTO.setIsPayment(false);

        String requestBody = objectMapper.writeValueAsString(expenseDTO);

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/update", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User authentication failed."));

        verify(theUserDAOImpl, times(1)).findUserByName(anyString());
    }

    @Test
    @WithMockUser(username = "user")
    void updateExpense_GroupNotFound () throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");

        when(theUserDAOImpl.findUserByName(anyString())).thenReturn(new User());
        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(null);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseName("Updated Expense");
        expenseDTO.setAmount(BigDecimal.valueOf(150.0));
        expenseDTO.setIsPayment(false);

        String requestBody = objectMapper.writeValueAsString(expenseDTO);

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/update", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Group not found."));

        verify(theUserDAOImpl, times(1)).findUserByName(anyString());
        verify(theGroupsDAOImpl, times(1)).findGroupById(anyInt());

    }


    @Test
    @WithMockUser(username = "user")
    void updateExpense_ExpenseNotFound () throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");

        when(theUserDAOImpl.findUserByName(anyString())).thenReturn(new User());
        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(new Groups());
        when(expensesDAO.findExpenseById(anyInt())).thenReturn(null);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseName("Updated Expense");
        expenseDTO.setAmount(BigDecimal.valueOf(150.0));
        expenseDTO.setIsPayment(false);

        String requestBody = objectMapper.writeValueAsString(expenseDTO);

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/update", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Expense not found or has been deleted."));

        verify(theUserDAOImpl, times(1)).findUserByName(anyString());
        verify(theGroupsDAOImpl, times(1)).findGroupById(anyInt());
        verify(expensesDAO, times(1)).findExpenseById(anyInt());

    }

    @Test
    @WithMockUser(username = "user")
    void updateExpense_InternalServerError () throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");

        when(theUserDAOImpl.findUserByName(anyString())).thenReturn(new User());
        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(new Groups());
        when(expensesDAO.findExpenseById(anyInt())).thenReturn(new Expenses());

        doThrow(new RuntimeException("Internal Server Error")).when(expensesDAO).updateExpense(any(Expenses.class));

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseName("Updated Expense");
        expenseDTO.setAmount(BigDecimal.valueOf(150.0));
        expenseDTO.setIsPayment(false);

        String requestBody = objectMapper.writeValueAsString(expenseDTO);

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/update", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to update expense: Internal Server Error"));

        verify(theUserDAOImpl, times(1)).findUserByName(anyString());
        verify(theGroupsDAOImpl, times(1)).findGroupById(anyInt());
        verify(expensesDAO, times(1)).findExpenseById(anyInt());
        verify(expensesDAO).updateExpense(any(Expenses.class));

    }

    @Test
    @WithMockUser(username = "user")
    void updateExpense_expenseDetailsNotFound () throws Exception {
        int groupId = 1;
        int expenseId = 1;

        User user = new User();
        user.setUsername("user");

        User payer = new User();
        payer.setUsername("payer");

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");
        when(theUserDAOImpl.findUserByName("user")).thenReturn(user);

        when(theGroupsDAOImpl.findGroupById(groupId)).thenReturn(new Groups());

        Expenses expense = new Expenses();
        expense.setId(expenseId);
        expense.setExpenseName("New Expense");
        expense.setAmount(BigDecimal.valueOf(100.0));
        expense.setDateCreated(new Date());
        expense.setAddedBy(user);
        expense.setDeleted(false);
        expense.setPayment(false);

        when(expensesDAO.findExpenseById(expenseId)).thenReturn(expense);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseName("Updated Expense");
        expenseDTO.setAmount(BigDecimal.valueOf(150.0));
        expenseDTO.setIsPayment(false);

        Map<String, BigDecimal> payers = new HashMap<>();
        payers.put("payer", BigDecimal.valueOf(150.0));
        expenseDTO.setPayers(payers);

        Map<String, Boolean> participants = new HashMap<>();
        participants.put("user", true);
        expenseDTO.setParticipants(participants);

        when(expenseParticipantsDAO.findParticipant(expenseId, "payer")).thenReturn(null);
        when(theUserDAOImpl.findUserByName("payer")).thenReturn(payer);

        when(expenseParticipantsDAO.findParticipant(expenseId, "user")).thenReturn(null);
        when(theUserDAOImpl.findUserByName("user")).thenReturn(user);


        String requestBody = objectMapper.writeValueAsString(expenseDTO);

        when(expenseService.getExpenseDetails(expenseId, groupId, "user")).thenReturn(null);

        mockMvc.perform(put("/splitwise/groups/{groupId}/expenses/{expenseId}/update", groupId, expenseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Expense not found or has been deleted"));


        verify(theUserDAOImpl, times(2)).findUserByName("user");
        verify(theUserDAOImpl, times(1)).findUserByName("payer");
        verify(theGroupsDAOImpl, times(1)).findGroupById(groupId);
        verify(expensesDAO, times(1)).updateExpense(any(Expenses.class));
        verify(expenseParticipantsDAO, times(2)).save(any(ExpenseParticipants.class));
        verify(expenseService, times(1)).getExpenseDetails(anyInt(), anyInt(), anyString());
    }

}
