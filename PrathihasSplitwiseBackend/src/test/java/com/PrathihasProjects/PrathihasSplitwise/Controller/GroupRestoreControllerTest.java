package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.configuration.TestSecurityConfig;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupRestoreController.class)
@Import(TestSecurityConfig.class)
public class GroupRestoreControllerTest {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private GroupsDAOImpl theGroupsDAOImpl;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user")
    void restoreGroup_success () throws Exception {

        when(theGroupsDAOImpl.restoreGroup(anyInt())).thenReturn(true);

        mockMvc.perform(put("/splitwise/groups/1/restore")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Group restored successfully."));

        verify(theGroupsDAOImpl).restoreGroup(anyInt());
    }

    @Test
    @WithMockUser(username = "user")
    void GroupSettleUpController_GroupNotFoundOrAlreadyActive () throws Exception {

        when(theGroupsDAOImpl.restoreGroup(anyInt())).thenReturn(false);

        mockMvc.perform(put("/splitwise/groups/1/restore")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Group not found or already active."));

        verify(theGroupsDAOImpl).restoreGroup(anyInt());
    }

    @Test
    @WithMockUser(username = "user")
    void GroupSettleUpController_InternalServerError () throws Exception {

        when(theGroupsDAOImpl.restoreGroup(anyInt())).thenThrow(new RuntimeException("An error occurred"));

        mockMvc.perform(put("/splitwise/groups/1/restore")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to restore group: An error occurred"));

        verify(theGroupsDAOImpl).restoreGroup(anyInt());
    }
}
