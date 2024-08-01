
package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.configuration.TestSecurityConfig;
import com.PrathihasProjects.PrathihasSplitwise.dao.ExpensesDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import com.PrathihasProjects.PrathihasSplitwise.helper.GroupMembersHelper;
import com.PrathihasProjects.PrathihasSplitwise.helper.Transaction;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(GetGroupDetailsController.class)
@Import(TestSecurityConfig.class)
public class GetGroupDetailsControllerTest {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private GroupsDAOImpl theGroupsDAOImpl;

    @MockBean
    private GroupMembersDAOImpl groupMembersDAO;

    @MockBean
    private ExpensesDAOImpl expensesDAO;

    @MockBean
    private GroupDetailsService groupDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user1")
    void getGroupDetails_success () throws Exception
    {
        User user = new User();
        user.setUsername("user1");

        Groups group = new Groups();
        group.setId(1);
        group.setGroupName("group name");
        group.setCreatedBy(user);
        group.setDeleted(false);
        group.setGroupDescription("group description");
        group.setSettledUp(false);
        group.setDateCreated(new Date());

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(group);

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");

        GroupMembersHelper gmDetails = new GroupMembersHelper("user1", 1, "user1", new Date());
        when(groupDetailsService.getGmDetails(anyInt(), anyString())).thenReturn(gmDetails);

        List<String> members = Arrays.asList("user1", "user2");
        when(groupMembersDAO.findMembersByGroupId(anyInt())).thenReturn(members);


        List<Expenses> expenses = Arrays.asList(Mockito.mock(Expenses.class),Mockito.mock(Expenses.class));
        when(expensesDAO.groupExpenses(anyInt())).thenReturn(expenses);


        Transaction transaction1 = new Transaction("user1", "user2", new BigDecimal("50.00" ));
        Transaction transaction2 = new Transaction("user2", "user1", new BigDecimal("25.00"));

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(groupDetailsService.getAllTransactions(anyList())).thenReturn(transactions);

        mockMvc.perform(get("/splitwise/groups/{groupId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.group.groupName", is("group name")))
                .andExpect(jsonPath("$.group.groupDescription", is("group description")))
                .andExpect(jsonPath("$.group.createdBy", is("user1")))
                .andExpect(jsonPath("$.group.settledUp", is(false)))
                .andExpect(jsonPath("$.group.deleted", is(false)))
                .andExpect(jsonPath("$.gmDetails.username", is("user1")))
                .andExpect(jsonPath("$.gmDetails.groupId", is(1)))
                .andExpect(jsonPath("$.gmDetails.addedBy", is("user1")))
                .andExpect(jsonPath("$.gmDetails.addedDate").exists())
                .andExpect(jsonPath("$.gmDetails.removedBy", is((String) null)))
                .andExpect(jsonPath("$.gmDetails.removedDate", is((String) null)))
                .andExpect(jsonPath("$.members", hasSize(2)))
                .andExpect(jsonPath("$.members[0]", is("user1")))
                .andExpect(jsonPath("$.members[1]", is("user2")))
                .andExpect(jsonPath("$.transactions", hasSize(2)))
                .andExpect(jsonPath("$.transactions[0].fromUser", is("user1")))
                .andExpect(jsonPath("$.transactions[0].toUser", is("user2")))
                .andExpect(jsonPath("$.transactions[0].amount", is(50.0)))
                .andExpect(jsonPath("$.transactions[1].fromUser", is("user2")))
                .andExpect(jsonPath("$.transactions[1].toUser", is("user1")))
                .andExpect(jsonPath("$.transactions[1].amount", is(25.0)));

        verify(theGroupsDAOImpl, times(1)).findGroupById(anyInt());
        verify(groupDetailsService, times(1)).getGmDetails(anyInt(), anyString());
        verify(groupMembersDAO, times(1)).findMembersByGroupId(anyInt());
        verify(expensesDAO, times(1)).groupExpenses(anyInt());
        verify(groupDetailsService, times(1)).getAllTransactions(anyList());

    }

    @Test
    @WithMockUser(username = "user")
    void getGroupDetails_groupNotFound () throws Exception{

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(null);
        mockMvc.perform(get("/splitwise/groups/{groupId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Group not found"));

        verify(theGroupsDAOImpl, times(1)).findGroupById(anyInt());
    }

    @Test
    @WithMockUser(username = "user1")
    void getGroupDetails_successWithDeletedAndSettledUpareTrue () throws Exception
    {
        User user = new User();
        user.setUsername("user1");

        User user3 = new User();
        user3.setUsername("user3");

        Groups group = new Groups();
        group.setId(1);
        group.setGroupName("group name");
        group.setCreatedBy(user);
        group.setDeleted(true);
        group.setGroupDescription("group description");
        group.setSettledUp(true);
        group.setDateCreated(new Date());
        group.setDeletedBy(user);
        group.setSettledBy(user3);
        group.setDeletedDate(new Date());
        group.setSettledDate(new Date());

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(group);

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user1");

        GroupMembersHelper gmDetails = new GroupMembersHelper("user1", 1, "user1", new Date());
        when(groupDetailsService.getGmDetails(anyInt(), anyString())).thenReturn(gmDetails);

        List<String> members = Arrays.asList("user1", "user2");
        when(groupMembersDAO.findMembersByGroupId(anyInt())).thenReturn(members);


        List<Expenses> expenses = Arrays.asList(Mockito.mock(Expenses.class),Mockito.mock(Expenses.class));
        when(expensesDAO.groupExpenses(anyInt())).thenReturn(expenses);


        Transaction transaction1 = new Transaction("user1", "user2", new BigDecimal("50.00" ));
        Transaction transaction2 = new Transaction("user2", "user1", new BigDecimal("25.00"));

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(groupDetailsService.getAllTransactions(anyList())).thenReturn(transactions);

        mockMvc.perform(get("/splitwise/groups/{groupId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.group.groupName", is("group name")))
                .andExpect(jsonPath("$.group.groupDescription", is("group description")))
                .andExpect(jsonPath("$.group.createdBy", is("user1")))
                .andExpect(jsonPath("$.group.settledUp", is(true)))
                .andExpect(jsonPath("$.group.deleted", is(true)))
                .andExpect(jsonPath("$.group.deletedBy", is("user1")))
                .andExpect(jsonPath("$.group.settledBy", is("user3")))
                .andExpect(jsonPath("$.group.deletedDate").exists())
                .andExpect(jsonPath("$.group.settledDate").exists())
                .andExpect(jsonPath("$.gmDetails.username", is("user1")))
                .andExpect(jsonPath("$.gmDetails.groupId", is(1)))
                .andExpect(jsonPath("$.gmDetails.addedBy", is("user1")))
                .andExpect(jsonPath("$.gmDetails.addedDate").exists())
                .andExpect(jsonPath("$.gmDetails.removedBy", is((String) null)))
                .andExpect(jsonPath("$.gmDetails.removedDate", is((String) null)))
                .andExpect(jsonPath("$.members", hasSize(2)))
                .andExpect(jsonPath("$.members[0]", is("user1")))
                .andExpect(jsonPath("$.members[1]", is("user2")))
                .andExpect(jsonPath("$.transactions", hasSize(2)))
                .andExpect(jsonPath("$.transactions[0].fromUser", is("user1")))
                .andExpect(jsonPath("$.transactions[0].toUser", is("user2")))
                .andExpect(jsonPath("$.transactions[0].amount", is(50.0)))
                .andExpect(jsonPath("$.transactions[1].fromUser", is("user2")))
                .andExpect(jsonPath("$.transactions[1].toUser", is("user1")))
                .andExpect(jsonPath("$.transactions[1].amount", is(25.0)));

        verify(theGroupsDAOImpl, times(1)).findGroupById(anyInt());
        verify(groupDetailsService, times(1)).getGmDetails(anyInt(), anyString());
        verify(groupMembersDAO, times(1)).findMembersByGroupId(anyInt());
        verify(expensesDAO, times(1)).groupExpenses(anyInt());
        verify(groupDetailsService, times(1)).getAllTransactions(anyList());

    }

    @Test
    @WithMockUser(username = "user")
    public void testGetGroupDetails_InternalServerError() throws Exception {
        when(theGroupsDAOImpl.findGroupById(anyInt())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/splitwise/groups/{groupId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Database error"));

        verify(theGroupsDAOImpl, times(1)).findGroupById(anyInt());
        verify(groupDetailsService, times(0)).getGmDetails(anyInt(), anyString());
        verify(groupMembersDAO, times(0)).findMembersByGroupId(anyInt());
        verify(expensesDAO, times(0)).groupExpenses(anyInt());
        verify(groupDetailsService, times(0)).getAllTransactions(anyList());
    }
}
