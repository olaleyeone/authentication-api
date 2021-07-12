package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.test.entity.EntityTest;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PortalUserIdentifierTest extends EntityTest {

    @Test
    void shouldNotSaveWithoutUser() {
        PortalUserIdentifier userIdentifier = new PortalUserIdentifier();
        userIdentifier.setIdentifier(UUID.randomUUID().toString());
        userIdentifier.setIdentifierType(UserIdentifierType.EMAIL_ADDRESS);
        assertThrows(PersistenceException.class, () -> saveAndFlush(userIdentifier));
    }

    @Test
    void shouldSaveWithUser() {
        String identifier = UUID.randomUUID().toString();
        UserIdentifierType identifierType = UserIdentifierType.EMAIL_ADDRESS;
        PortalUser user = modelFactory.create(PortalUser.class);

        PortalUserIdentifier userIdentifier = new PortalUserIdentifier();
        userIdentifier.setIdentifier(identifier);
        userIdentifier.setIdentifierType(identifierType);
        userIdentifier.setPortalUser(user);
        saveAndFlush(userIdentifier);
        entityManager.refresh(userIdentifier);
        assertEquals(identifier, userIdentifier.getIdentifier());
        assertEquals(identifierType, userIdentifier.getIdentifierType());
        assertEquals(user.getId(), userIdentifier.getPortalUser().getId());
        assertNotNull(userIdentifier.getCreatedAt());
    }
}
