package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.entitytest.EntityTest;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PortalUserTest extends EntityTest {

    @Test
    void saveUser() {
        PortalUser user = new PortalUser();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        saveAndFlush(user);
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedOn());
    }

    @Test
    void shouldNotOverwriteCreatedOn() {
        LocalDateTime then = LocalDateTime.now().minusDays(1);
        PortalUser user = new PortalUser();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setCreatedOn(then);
        saveAndFlush(user);
        entityManager.refresh(user);
        assertEquals(then, user.getCreatedOn());
    }

    @Test
    void shouldNotSaveUserWithoutFirstName() {
        PortalUser user = new PortalUser();
        user.setLastName(faker.name().lastName());
        assertThrows(PersistenceException.class, () -> saveAndFlush(user));
    }

    @Test
    void dateCreatedShouldBeImmutable() {
        String password = UUID.randomUUID().toString();

        PortalUser user = modelFactory.pipe(PortalUser.class)
                .then(it -> {
                    it.setPassword(password);
                    return it;
                })
                .create();

        LocalDateTime dateCreated = user.getCreatedOn();
        user.setPassword(UUID.randomUUID().toString());
        saveAndFlush(user);
        entityManager.refresh(user);
        assertNotEquals(password, user.getPassword());
        assertEquals(dateCreated, user.getCreatedOn());
    }
}