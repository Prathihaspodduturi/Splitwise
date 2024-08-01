package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.configuration.TestSecurityConfig;
import com.PrathihasProjects.PrathihasSplitwise.dao.UserDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import com.PrathihasProjects.PrathihasSplitwise.jwt.JwtUtil;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(SignUpController.class)
@Import(TestSecurityConfig.class)
public class SignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDAOImpl userDAO;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void testSIgnUpController_UserAlreadyExists () throws Exception{

        String username = "existing user";

        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setPassword("password");

        when(userDAO.findUserByName(username)).thenReturn(existingUser);

        mockMvc.perform(post("/splitwise/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"existing user\", \"password\": \"password\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("User already exists"));
    }

    @Test
    void testSignUpController_UserSignUp() throws Exception {
        String username = "newUser";

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword("password123");

        when(userDAO.findUserByName(username)).thenReturn(null);

        mockMvc.perform(post("/splitwise/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"newUser\", \"password\": \"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Signup successfull"));

        verify(userDAO).findUserByName(username);
        verify(userDAO).save(any(User.class));
    }

    @Test
    void testSignUpController_InternalServerError() throws Exception {
        String username = "newUser";

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword("password123");

        // Mocking an exception to be thrown when trying to find a user
        when(userDAO.findUserByName(any(String.class))).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/splitwise/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"newUser\", \"password\": \"password123\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred!"));
    }
}
