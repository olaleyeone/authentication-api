package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.dto.data.UserRegistrationApiRequest;
import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.service.UserRegistrationService;
import com.olaleyeone.auth.test.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRegistrationControllerTest extends ControllerTest {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private UserApiResponseHandler userApiResponseHandler;

    private UserRegistrationApiRequest userRegistrationApiRequest;

    @BeforeEach
    void setUp() {
        userRegistrationApiRequest = new UserRegistrationApiRequest();
        userRegistrationApiRequest.setFirstName(faker.name().firstName());
        userRegistrationApiRequest.setPhoneNumber(faker.phoneNumber().phoneNumber());
        userRegistrationApiRequest.setEmail(faker.internet().emailAddress());
        userRegistrationApiRequest.setPassword(faker.internet().password());
    }

    @Test
    void loginWithCorrectCredentials() throws Exception {

        UserApiResponse userApiResponse = new UserApiResponse();
        PortalUser portalUser = new PortalUser();

        Mockito.when(userRegistrationService.registerUser(Mockito.any()))
                .then(invocation -> portalUser);
        Mockito.when(userApiResponseHandler.getUserApiResponse(Mockito.any(PortalUser.class)))
                .then(invocation -> userApiResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationApiRequest)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    UserApiResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), UserApiResponse.class);
                    assertNotNull(response);
                    assertEquals(userApiResponse, response);
                });
        Mockito.verify(userRegistrationService, Mockito.times(1))
                .registerUser(userRegistrationApiRequest);
    }
}