package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.dto.data.UserRegistrationApiRequest;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.service.UserRegistrationService;
import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.validator.UniqueIdentifierValidator;
import com.olaleyeone.auth.validator.ValidPhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRegistrationControllerTest extends ControllerTest {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private AccessTokenApiResponseHandler accessTokenApiResponseHandler;

    @Autowired
    private UniqueIdentifierValidator uniqueIdentifierValidator;

    @Autowired
    private ValidPhoneNumberValidator validPhoneNumberValidator;

    private UserRegistrationApiRequest userRegistrationApiRequest;
    private AccessTokenApiResponse accessTokenApiResponse;

    @BeforeEach
    void setUp() {
        userRegistrationApiRequest = new UserRegistrationApiRequest();
        userRegistrationApiRequest.setFirstName(faker.name().firstName());
        userRegistrationApiRequest.setPhoneNumber(faker.phoneNumber().phoneNumber());
        userRegistrationApiRequest.setEmail(faker.internet().emailAddress());
        userRegistrationApiRequest.setPassword(faker.internet().password());

        Mockito.doReturn(true)
                .when(validPhoneNumberValidator).isValid(Mockito.anyString(), Mockito.any());
        Mockito.doReturn(true)
                .when(uniqueIdentifierValidator).isValid(Mockito.anyString(), Mockito.any());

        accessTokenApiResponse = new AccessTokenApiResponse();
        Mockito.when(accessTokenApiResponseHandler.getAccessToken(Mockito.any()))
                .then(invocation -> ResponseEntity.ok(accessTokenApiResponse));
    }

    @Test
    void registerUser() throws Exception {

        PortalUserAuthentication userAuthentication = new PortalUserAuthentication();

        Mockito.when(userRegistrationService.registerUser(Mockito.any(), Mockito.any()))
                .then(invocation -> userAuthentication);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .with(body(userRegistrationApiRequest)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    AccessTokenApiResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), AccessTokenApiResponse.class);
                    assertEquals(accessTokenApiResponse, response);
                });
        Mockito.verify(userRegistrationService, Mockito.times(1))
                .registerUser(Mockito.eq(userRegistrationApiRequest), Mockito.any());
    }

    @Test
    void shouldValidateRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .with(body(userRegistrationApiRequest)));

        Mockito.verify(validPhoneNumberValidator, Mockito.times(1))
                .isValid(Mockito.eq(userRegistrationApiRequest.getPhoneNumber()), Mockito.any());

        Mockito.verify(uniqueIdentifierValidator, Mockito.times(1))
                .isValid(Mockito.eq(userRegistrationApiRequest.getEmail()), Mockito.any());
        Mockito.verify(uniqueIdentifierValidator, Mockito.times(1))
                .isValid(Mockito.eq(userRegistrationApiRequest.getPhoneNumber()), Mockito.any());
    }
}