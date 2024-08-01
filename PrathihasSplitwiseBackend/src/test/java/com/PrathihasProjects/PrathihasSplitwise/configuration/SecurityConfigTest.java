package com.PrathihasProjects.PrathihasSplitwise.configuration;

import com.PrathihasProjects.PrathihasSplitwise.jwt.JwtRequestFilter;
import com.PrathihasProjects.PrathihasSplitwise.jwt.JwtUtil;
import com.PrathihasProjects.PrathihasSplitwise.services.MyUserDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(TestSecurityConfig.class)
@Import(SecurityConfig.class)
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    public void testCorsConfiguration() throws Exception {
        mockMvc.perform(get("/public-endpoint").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void testPublicEndpointsAreAccessible() throws Exception {
        mockMvc.perform(get("/splitwise/").with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/splitwise/login").with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/splitwise/signup").with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/heartbeat").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testAuthenticatedAccessToProtectedEndpoints() throws Exception {
        mockMvc.perform(get("/splitwise/secure-endpoint").with(csrf()))
                .andExpect(status().isOk());
    }

}
