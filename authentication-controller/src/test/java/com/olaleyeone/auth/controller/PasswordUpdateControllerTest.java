package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.security.access.AccessStatus;
import com.olaleyeone.auth.security.authorizer.NotClientTokenAuthorizer;
import com.olaleyeone.auth.test.ControllerTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PasswordUpdateControllerTest extends ControllerTest {

    @Autowired
    private NotClientTokenAuthorizer authorizer;

    @Test
    void changePassword() throws Exception {
        Mockito.doReturn(AccessStatus.allowed()).when(authorizer).getStatus(Mockito.any(), Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.post("/password").with(loggedInUser))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRequireNonClientToken() throws Exception {
        Mockito.doReturn(AccessStatus.denied()).when(authorizer).getStatus(Mockito.any(), Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.post("/password").with(loggedInUser))
                .andExpect(status().isForbidden());
    }
}