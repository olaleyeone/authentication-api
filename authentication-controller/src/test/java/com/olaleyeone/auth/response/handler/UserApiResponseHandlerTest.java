package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.web.exception.NotFoundException;
import com.olaleyeone.auth.repository.PortalUserRepository;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserApiResponseHandlerTest extends ComponentTest {

    @Mock
    private PortalUserRepository portalUserRepository;

    @InjectMocks
    private UserApiResponseHandler userApiResponseHandler;

    private PortalUser user;

    @BeforeEach
    void setUp() {

        user = new PortalUser();
        user.setId(faker.number().randomNumber());
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
    }

    @Test
    void getUserApiResponse() {
        Mockito.doReturn(Optional.of(user)).when(portalUserRepository).findById(Mockito.any());
        UserApiResponse userApiResponse = userApiResponseHandler.getUserApiResponse(user.getId());
        assertNotNull(userApiResponse);
        assertEquals(user.getId(), userApiResponse.getId());
        assertEquals(user.getFirstName(), userApiResponse.getFirstName());
        assertEquals(user.getLastName(), userApiResponse.getLastName());
        Mockito.verify(portalUserRepository, Mockito.times(1))
                .findById(user.getId());
    }

    @Test
    void shouldFailIfUserIsNotFound() {
        Mockito.doReturn(Optional.empty()).when(portalUserRepository).findById(Mockito.any());
        assertThrows(NotFoundException.class, () -> userApiResponseHandler.getUserApiResponse(user.getId()));
    }
}