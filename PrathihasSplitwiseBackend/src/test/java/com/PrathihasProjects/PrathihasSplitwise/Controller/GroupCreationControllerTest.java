package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.Controller.GroupCreationController;
import com.PrathihasProjects.PrathihasSplitwise.configuration.TestSecurityConfig;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.UserDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dto.GroupDTO;
import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import com.PrathihasProjects.PrathihasSplitwise.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(GroupCreationController.class)
@Import(TestSecurityConfig.class)
public class GroupCreationControllerTest {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private GroupMembersDAOImpl groupMembersDAO;

    @MockBean
    private GroupsDAOImpl theGroupsDAOImpl;

    @MockBean
    private UserDAOImpl theUserDAOImpl;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user")
    void CreateGroup_Success () throws Exception {

        GroupDTO groupDTO = new GroupDTO("Test Group", "Test Description");

        User user = new User();
        user.setUsername("user");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        when(theUserDAOImpl.findUserByName(anyString())).thenReturn(user);

        Groups newGroup = new Groups();
        newGroup.setGroupName(groupDTO.getGroupName());
        newGroup.setSettledUp(false);
        newGroup.setDeleted(false);
        newGroup.setCreatedBy(user);
        newGroup.setGroupDescription(groupDTO.getGroupDescription());
        newGroup.setDateCreated(new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        String groupDTOJson = objectMapper.writeValueAsString(groupDTO);

        mockMvc.perform(post("/splitwise/creategroup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(groupDTOJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupName", is("Test Group")))
                .andExpect(jsonPath("$.groupDescription", is("Test Description")))
                .andExpect(jsonPath("$.createdBy.username", is("user")))
                .andExpect(jsonPath("$.settledUp", is(false)))
                .andExpect(jsonPath("$.deleted", is(false)));

        verify(theUserDAOImpl).findUserByName(anyString());
        verify(theGroupsDAOImpl).save(any(Groups.class));
        verify(groupMembersDAO).save(any(GroupMembers.class));

    }

    @Test
    @WithMockUser(username = "user")
    void CreateGroup_UserNotFound () throws Exception {

        GroupDTO groupDTO = new GroupDTO("Test Group", "Test Description");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        when(theUserDAOImpl.findUserByName(anyString())).thenReturn(null);

        ObjectMapper objectMapper = new ObjectMapper();
        String groupDTOJson = objectMapper.writeValueAsString(groupDTO);

        mockMvc.perform(post("/splitwise/creategroup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(groupDTOJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("technical error"));


        verify(theUserDAOImpl).findUserByName(anyString());

    }


    @Test
    @WithMockUser(username = "user")
    void CreateGroup_InternalServerError () throws Exception {

        GroupDTO groupDTO = new GroupDTO("Test Group", "Test Description");

        User user = new User();
        user.setUsername("user");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        when(theUserDAOImpl.findUserByName(anyString())).thenReturn(user);
        doThrow(new RuntimeException("Unable to create new group")).when(theGroupsDAOImpl).save(any());

        ObjectMapper objectMapper = new ObjectMapper();
        String groupDTOJson = objectMapper.writeValueAsString(groupDTO);

        mockMvc.perform(post("/splitwise/creategroup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(groupDTOJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to create group: Unable to create new group"));


        verify(theUserDAOImpl).findUserByName(anyString());

    }


}
