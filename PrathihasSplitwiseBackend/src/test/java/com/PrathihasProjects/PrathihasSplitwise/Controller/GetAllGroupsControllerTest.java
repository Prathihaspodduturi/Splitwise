package com.PrathihasProjects.PrathihasSplitwise.Controller;


import com.PrathihasProjects.PrathihasSplitwise.configuration.TestSecurityConfig;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GetAllGroupsController.class)
@Import(TestSecurityConfig.class)
public class GetAllGroupsControllerTest {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private GroupMembersDAOImpl groupMembersDAO;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser(username = "user")
    void getGroups_success () throws Exception {

        User user = new User();
        user.setUsername("user");

        User user1 = new User();
        user1.setUsername("user1");

        User user2 = new User();
        user2.setUsername("user2");

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");

        Groups group1 = new Groups();

        group1.setId(1);
        group1.setGroupName("group1");
        group1.setGroupDescription("group description1");
        group1.setDateCreated(new Date());
        group1.setSettledUp(false);
        group1.setDeleted(false);
        group1.setCreatedBy(user);

        Groups group2 = new Groups();

        group2.setId(2);
        group2.setGroupName("group2");
        group2.setGroupDescription("group description2");
        group2.setDateCreated(new Date());
        group2.setSettledUp(true);
        group2.setDeleted(true);
        group2.setCreatedBy(user);
        group2.setSettledBy(user1);
        group2.setDeletedBy(user2);
        group2.setSettledDate(new Date());
        group2.setDeletedDate(new Date());

        List<Groups> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);

        when(groupMembersDAO.findGroupsOfUser(anyString())).thenReturn(groups);

        GroupMembers gmDetails1 = new GroupMembers();
        gmDetails1.setRemovedDate(null);

        GroupMembers gmDetails2 = new GroupMembers();
        gmDetails2.setRemovedDate(new Date());

        when(groupMembersDAO.getDetails(anyInt(), anyString())).thenReturn(gmDetails1, gmDetails2);

        mockMvc.perform(post("/splitwise/groups")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].groupName", is("group1")))
                .andExpect(jsonPath("$[0].groupDescription", is("group description1")))
                .andExpect(jsonPath("$[0].createdBy", is("user")))
                .andExpect(jsonPath("$[0].settledUp", is(false)))
                .andExpect(jsonPath("$[0].deleted", is(false)))
                .andExpect(jsonPath("$[0].removedDate", is(nullValue())))
                .andExpect(jsonPath("$[1].groupName", is("group2")))
                .andExpect(jsonPath("$[1].groupDescription", is("group description2")))
                .andExpect(jsonPath("$[1].createdBy", is("user")))
                .andExpect(jsonPath("$[1].settledUp", is(true)))
                .andExpect(jsonPath("$[1].deleted", is(true)))
                .andExpect(jsonPath("$[1].settledBy", is("user1")))
                .andExpect(jsonPath("$[1].deletedBy", is("user2")))
                .andExpect(jsonPath("$[1].removedDate").exists());

    }

    @Test
    @WithMockUser(username = "user")
    public void testGetGroups_InternalServerError() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn("user");
        when(groupMembersDAO.findGroupsOfUser(anyString())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/splitwise/groups")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Database error"));

        verify(groupMembersDAO, times(1)).findGroupsOfUser(anyString());
    }

}
