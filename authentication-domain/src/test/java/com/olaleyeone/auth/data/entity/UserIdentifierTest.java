package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.test.EntityTest;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserIdentifierTest extends EntityTest {

    @Test
    void shouldNotSaveWithoutUser() {
        UserIdentifier userIdentifier = new UserIdentifier();
        userIdentifier.setIdentifier(UUID.randomUUID().toString());
        userIdentifier.setIdentifierType(UserIdentifierType.EMAIL);
        assertThrows(PersistenceException.class, () -> saveAndFlush(userIdentifier));
    }

    @Test
    void shouldSaveWithUser() {
        String identifier = UUID.randomUUID().toString();
        UserIdentifierType identifierType = UserIdentifierType.EMAIL;
        User user = modelFactory.create(User.class);

        UserIdentifier userIdentifier = new UserIdentifier();
        userIdentifier.setIdentifier(identifier);
        userIdentifier.setIdentifierType(identifierType);
        userIdentifier.setUser(user);
        saveAndFlush(userIdentifier);
        entityManager.refresh(userIdentifier);
        assertEquals(identifier, userIdentifier.getIdentifier());
        assertEquals(identifierType, userIdentifier.getIdentifierType());
        assertEquals(user.getId(), userIdentifier.getUser().getId());
        assertNotNull(userIdentifier.getDateCreated());
    }
}
