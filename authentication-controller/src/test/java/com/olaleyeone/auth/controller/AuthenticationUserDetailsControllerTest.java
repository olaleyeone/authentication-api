package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.repository.PortalUserRepository;
import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationUserDetailsControllerTest extends ControllerTest {

    @Autowired
    private PortalUserRepository portalUserRepository;

    @Autowired
    private UserApiResponseHandler userApiResponseHandler;

    @Test
    void getAccountDetails() throws Exception {
        PortalUser portalUser = new PortalUser();
        portalUser.setId(faker.number().randomNumber());
        Mockito.doReturn(Optional.of(portalUser)).when(portalUserRepository).findById(Mockito.any());
        Mockito.doReturn(portalUser.getId().toString()).when(accessClaims).getSubject();
        mockMvc.perform(MockMvcRequestBuilders.get("/me")
                .with(loggedInUser))
                .andExpect(status().isOk());
        Mockito.verify(portalUserRepository, Mockito.times(1))
                .findById(portalUser.getId());
        Mockito.verify(userApiResponseHandler, Mockito.times(1)).toUserApiResponse(portalUser);
    }
}