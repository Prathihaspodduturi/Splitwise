package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.configuration.TestSecurityConfig;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
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

import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupSettleUpController.class)
@Import(TestSecurityConfig.class)
public class GroupSettleUpControllerTest {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private GroupsDAOImpl theGroupsDAOImpl;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user")
    void GroupSettleUpController_Success () throws Exception
    {

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        mockMvc.perform(put("/splitwise/groups/1/settlegroup")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("succesfully settled up group"));

        verify(theGroupsDAOImpl).settlegroupById(anyInt(), anyString(), any(Date.class));

    }

    @Test
    @WithMockUser(username = "user")
    void GroupSettleUpController_InternalServerError () throws Exception
    {

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        when(theGroupsDAOImpl.settlegroupById(anyInt(), anyString(), Mockito.any(Date.class))).thenThrow(new RuntimeException("An error occurred"));

        mockMvc.perform(put("/splitwise/groups/1/settlegroup")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Please try again later"));

        verify(theGroupsDAOImpl).settlegroupById(anyInt(), anyString(), any(Date.class));


    }
}
