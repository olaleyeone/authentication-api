package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.search.handler.PortalUserAuthenticationSearchHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationHistorySearchControllerTest extends ControllerTest {

    @Autowired
    private PortalUserAuthenticationSearchHandler searchHandler;

    @Test
    void searchUserSessions() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/me/sessions")
                .with(loggedInUser))
                .andExpect(status().isOk())
                .andExpect(result -> {
//                    UserApiResponse response = objectMapper.readValue(result.getResponse().getContentAsByteArray(), UserApiResponse.class);
//                    assertNotNull(response);
//                    assertEquals(userApiResponse.getId(), response.getId());
                });
        Mockito.verify(searchHandler, Mockito.times(1))
                .search(Mockito.any(), Mockito.any());
    }

    @Test
    void searchUserSessionsById() throws Exception {
        int id = 100;
        mockMvc.perform(MockMvcRequestBuilders.get("/me/sessions")
                .param("id", String.valueOf(id))
                .with(loggedInUser))
                .andExpect(status().isOk());
        Mockito.verify(searchHandler, Mockito.times(1))
                .search(Mockito.argThat(filter -> {
                    assertEquals(id, filter.getId());
                    return true;
                }), Mockito.any());
    }
}