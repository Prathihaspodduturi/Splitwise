package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.configuration.TestSecurityConfig;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.UserDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.Expenses;
import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import com.PrathihasProjects.PrathihasSplitwise.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AddMemberToGroupController.class)
@Import(TestSecurityConfig.class)
public class AddMemberToGroupControllerTest {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private GroupsDAOImpl theGroupsDAOImpl;

    @MockBean
    private UserDAOImpl theUserDAOImpl;

    @MockBean
    private GroupMembersDAOImpl groupMembersDAO;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user")
    void addMemberToGroup_success () throws Exception {
        User user = new User();
        user.setUsername("user");

        User userToAdd = new User();
        userToAdd.setUsername("newUser");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        when(theUserDAOImpl.findUserByName("user")).thenReturn(user);
        when(theUserDAOImpl.findUserByName("newUser")).thenReturn(userToAdd);

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(new Groups());

        when(groupMembersDAO.isMember(anyString(), anyInt())).thenReturn(false);

        when(groupMembersDAO.isOldMember(anyString(), anyInt())).thenReturn(false);

        List<String> members = Arrays.asList("user", "newUser");
        when(groupMembersDAO.findMembersByGroupId(anyInt())).thenReturn(members);

        String requestBody = "{\"newUsername\": \"newUser\"}";

        mockMvc.perform(post("/splitwise/groups/{groupId}/addmember", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"user\", \"newUser\"]"));

        verify(theUserDAOImpl, times(1)).findUserByName("user");
        verify(theUserDAOImpl, times(1)).findUserByName("newUser");
        verify(theGroupsDAOImpl, times(1)).findGroupById(anyInt());
        verify(groupMembersDAO, times(1)).isMember(anyString(), anyInt());
        verify(groupMembersDAO, times(1)).isMember(anyString(), anyInt());
        verify(groupMembersDAO, times(1)).save(any(GroupMembers.class));
        verify(groupMembersDAO, times(1)).findMembersByGroupId(anyInt());

    }

    @Test
    @WithMockUser(username = "user")
    void addMemberToGroup_noUsernameInTheRequestBody () throws Exception {

        String requestBody = "{\"newUsername\": \"\"}";

        mockMvc.perform(post("/splitwise/groups/{groupId}/addmember", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username is required"));

        verify(theUserDAOImpl, times(0)).findUserByName("user");
    }

    @Test
    @WithMockUser(username = "user")
    void addMemberToGroup_userNotFound() throws Exception {

        String newUsername = "newUser";

        when(theUserDAOImpl.findUserByName("user")).thenReturn(new User());
        when(theUserDAOImpl.findUserByName(newUsername)).thenReturn(null);

        String requestBody = "{\"newUsername\": \"newUser\"}";

        mockMvc.perform(post("/splitwise/groups/{groupId}/addmember", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User does not exist"));

        verify(groupMembersDAO, times(0)).save(any(GroupMembers.class));
    }

    @Test
    @WithMockUser(username = "user")
    void addMemberToGroup_groupNotFound() throws Exception {
        int groupId = 1;
        String newUsername = "newUser";

        when(theUserDAOImpl.findUserByName("user")).thenReturn(new User());
        when(theUserDAOImpl.findUserByName(newUsername)).thenReturn(new User());

        when(theGroupsDAOImpl.findGroupById(groupId)).thenReturn(null);

        String requestBody = "{\"newUsername\": \"newUser\"}";

        mockMvc.perform(post("/splitwise/groups/{groupId}/addmember", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(groupMembersDAO, times(0)).save(any(GroupMembers.class));
    }


    @Test
    @WithMockUser(username = "user")
    void addMemberToGroup_userIsAlreadyMemberOfTheGroup() throws Exception {
        int groupId = 1;
        String newUsername = "newUser";

        when(theUserDAOImpl.findUserByName("user")).thenReturn(new User());
        when(theUserDAOImpl.findUserByName(newUsername)).thenReturn(new User());

        when(theGroupsDAOImpl.findGroupById(groupId)).thenReturn(new Groups());

        when(groupMembersDAO.isMember(newUsername, groupId )).thenReturn(true);

        String requestBody = "{\"newUsername\": \"newUser\"}";

        mockMvc.perform(post("/splitwise/groups/{groupId}/addmember", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User is already a member of this group"));

        verify(groupMembersDAO, times(0)).save(any(GroupMembers.class));
    }

    @Test
    @WithMockUser(username = "user")
    void addMemberToGroup_userIsOldMemberOfTheGroup() throws Exception {
        int groupId = 1;
        String newUsername = "newUser";

        User curUser = new User();
        curUser.setUsername("user");

        User userToAdd = new User();
        userToAdd.setUsername(newUsername);

        when(theUserDAOImpl.findUserByName("user")).thenReturn(new User());
        when(theUserDAOImpl.findUserByName(newUsername)).thenReturn(new User());

        when(theGroupsDAOImpl.findGroupById(groupId)).thenReturn(new Groups());

        when(groupMembersDAO.isMember(newUsername, groupId )).thenReturn(false);

        when(groupMembersDAO.isOldMember(newUsername, groupId)).thenReturn(true);

        GroupMembers gm = new GroupMembers();
        gm.setUser(userToAdd);

        when(groupMembersDAO.getDetails(groupId, newUsername)).thenReturn(gm);

        List<String> members = Arrays.asList("user", "newUser");
        when(groupMembersDAO.findMembersByGroupId(anyInt())).thenReturn(members);

        String requestBody = "{\"newUsername\": \"newUser\"}";

        mockMvc.perform(post("/splitwise/groups/{groupId}/addmember", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"user\", \"newUser\"]"));

        verify(theUserDAOImpl, times(1)).findUserByName("user");
        verify(theUserDAOImpl, times(1)).findUserByName("newUser");
        verify(theGroupsDAOImpl, times(1)).findGroupById(anyInt());
        verify(groupMembersDAO, times(1)).isMember(anyString(), anyInt());
        verify(groupMembersDAO, times(1)).isOldMember(anyString(), anyInt());
        verify(groupMembersDAO, times(1)).getDetails(anyInt(), anyString());
        verify(groupMembersDAO, times(1)).save(any(GroupMembers.class));
        verify(groupMembersDAO, times(1)).findMembersByGroupId(anyInt());
    }

    @Test
    @WithMockUser(username = "user")
    void addMemberToGroup_internalServerError() throws Exception {
        int groupId = 1;
        String newUsername = "newUser";

        when(theUserDAOImpl.findUserByName("user")).thenReturn(new User());
        when(theUserDAOImpl.findUserByName(newUsername)).thenReturn(new User());

        when(theGroupsDAOImpl.findGroupById(groupId)).thenReturn(new Groups());

        when(groupMembersDAO.isMember(newUsername, groupId )).thenReturn(false);

        when(groupMembersDAO.isOldMember(newUsername, groupId)).thenReturn(false);

        doThrow(new RuntimeException("Internal server error")).when(groupMembersDAO).save(any(GroupMembers.class));

        String requestBody = "{\"newUsername\": \"newUser\"}";

        mockMvc.perform(post("/splitwise/groups/{groupId}/addmember", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: please try again later!"));

        verify(theUserDAOImpl, times(1)).findUserByName("user");
        verify(theUserDAOImpl, times(1)).findUserByName("newUser");
        verify(theGroupsDAOImpl, times(1)).findGroupById(anyInt());
        verify(groupMembersDAO, times(1)).isMember(anyString(), anyInt());
        verify(groupMembersDAO, times(1)).isOldMember(anyString(), anyInt());
        verify(groupMembersDAO, times(1)).save(any(GroupMembers.class));
        verify(groupMembersDAO, times(0)).findMembersByGroupId(anyInt());
    }

}
