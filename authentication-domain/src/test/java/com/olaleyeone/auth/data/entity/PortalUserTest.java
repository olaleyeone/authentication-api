package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.test.entity.EntityTest;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
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
        OffsetDateTime then = OffsetDateTime.now().minusDays(1);
        PortalUser user = new PortalUser();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setCreatedOn(then);
        saveAndFlush(user);
        entityManager.refresh(user);
        assertEquals(then, user.getCreatedOn());
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

        OffsetDateTime dateCreated = user.getCreatedOn();
        user.setPassword(UUID.randomUUID().toString());
        saveAndFlush(user);
        entityManager.refresh(user);
        assertNotEquals(password, user.getPassword());
        assertEquals(dateCreated, user.getCreatedOn());
    }
}