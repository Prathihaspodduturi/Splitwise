package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.configuration.TestSecurityConfig;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupUpdationController.class)
@Import(TestSecurityConfig.class)
public class GroupUpdationControllerTest {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private GroupsDAOImpl theGroupsDAOImpl;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "User")
    void updateGroupName_Success () throws Exception{

        Groups group = new Groups();
        group.setGroupName("newName");

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(group);

        mockMvc.perform(put("/splitwise/groups/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"groupName\": \"newName\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupName").value("newName"));

        verify(theGroupsDAOImpl).findGroupById(anyInt());
        verify(theGroupsDAOImpl).updateGroupName(group);
    }

    @Test
    @WithMockUser(username = "User")
    void updateGroupName_newGroupNameIsEmptyOrNull () throws Exception{

        mockMvc.perform(put("/splitwise/groups/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"groupName\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Group name is required"));

    }

    @Test
    @WithMockUser(username = "User")
    void updateGroupName_groupNotFound () throws Exception{

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenReturn(null);

        mockMvc.perform(put("/splitwise/groups/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"groupName\": \"newName\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "User")
    void updateGroupName_InternalServerError () throws Exception{

        when(theGroupsDAOImpl.findGroupById(anyInt())).thenThrow(new RuntimeException("Unexpected Error"));

        mockMvc.perform(put("/splitwise/groups/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"groupName\": \"newName\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Please try again later"));
    }

}
