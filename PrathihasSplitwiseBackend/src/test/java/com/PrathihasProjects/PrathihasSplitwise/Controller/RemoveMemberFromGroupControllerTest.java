package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.configuration.TestSecurityConfig;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.UserDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import com.PrathihasProjects.PrathihasSplitwise.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RemoveMemberFromGroupController.class)
@Import(TestSecurityConfig.class)
public class RemoveMemberFromGroupControllerTest {

    @MockBean
    private GroupMembersDAOImpl groupMembersDAO;

    @MockBean
    private GroupsDAOImpl theGroupsDAOImpl;

    @MockBean
    private UserDAOImpl theUserDAOImpl;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser(username = "removerUser")
    void removeMember_removedSuccesfully () throws Exception {

        int groupId = 1;
        Groups group = new Groups();
        group.setId(groupId);

        User removerUser = new User();
        removerUser.setUsername("removerUser");
        User memberUser = new User();
        memberUser.setUsername("memberUser");

        GroupMembers groupMember = new GroupMembers();
        groupMember.setId(1);
        groupMember.setUser(memberUser);
        groupMember.setRemovedBy(removerUser);
        groupMember.setRemovedDate(new Date());

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(group);
        when(theUserDAOImpl.findUserByName("removerUser")).thenReturn(removerUser);
        when(theUserDAOImpl.findUserByName("memberUser")).thenReturn(memberUser);
        when(groupMembersDAO.getDetails(anyInt(), anyString())).thenReturn(groupMember);

        mockMvc.perform(put("/splitwise/groups/1/removemember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"memberUser\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Succesfull"));

        verify(groupMembersDAO).save(groupMember);
    }

    @Test
    @WithMockUser(username = "removerUser")
    void removeMember_memberUserisNullorEmpty () throws Exception {

        mockMvc.perform(put("/splitwise/groups/1/removemember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Member username is required"));
    }

    @Test
    @WithMockUser(username = "removerUser")
    void removeMember_groupNotFound () throws Exception {

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(null);

        mockMvc.perform(put("/splitwise/groups/1/removemember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"memberUser\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad Request"));
    }

    @Test
    @WithMockUser(username = "removerUser")
    void removeMember_memberUserNotFound() throws Exception {
        int groupId = 1;
        Groups group = new Groups();
        group.setId(groupId);

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(group);
        when(theUserDAOImpl.findUserByName("memberUser")).thenReturn(null);

        mockMvc.perform(put("/splitwise/groups/1/removemember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"memberUser\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User to be removed not found"));
    }

    @Test
    @WithMockUser(username = "removerUser")
    void removeMember_userNotPartOfGroup() throws Exception {
        int groupId = 1;
        Groups group = new Groups();
        group.setId(groupId);

        User removerUser = new User();
        removerUser.setUsername("removerUser");
        User memberUser = new User();
        memberUser.setUsername("memberUser");

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(group);
        when(theUserDAOImpl.findUserByName("removerUser")).thenReturn(removerUser);
        when(theUserDAOImpl.findUserByName("memberUser")).thenReturn(memberUser);
        when(groupMembersDAO.getDetails(anyInt(), anyString())).thenReturn(null);

        mockMvc.perform(put("/splitwise/groups/1/removemember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"memberUser\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User is not part of this group"));
    }

    @Test
    @WithMockUser(username = "removerUser")
    void removeMember_internalServerError() throws Exception {
        int groupId = 1;
        Groups group = new Groups();
        group.setId(groupId);

        User removerUser = new User();
        removerUser.setUsername("removerUser");
        User memberUser = new User();
        memberUser.setUsername("memberUser");

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(group);
        when(theUserDAOImpl.findUserByName("removerUser")).thenReturn(removerUser);
        when(theUserDAOImpl.findUserByName("memberUser")).thenReturn(memberUser);

        when(groupMembersDAO.getDetails(anyInt(), anyString())).thenThrow(new RuntimeException("Unexpected Error"));

        mockMvc.perform(put("/splitwise/groups/1/removemember")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"memberUser\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: please try again later!"));
    }

}
