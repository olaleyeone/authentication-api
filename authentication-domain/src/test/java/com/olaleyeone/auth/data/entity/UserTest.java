package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.test.EntityTest;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest extends EntityTest {

    @Test
    void saveUser() {
        User user = new User();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        saveAndFlush(user);
        assertNotNull(user.getId());
        assertNotNull(user.getDateCreated());
    }

    @Test
    void shouldNotSaveUserWithoutFirstName() {
        User user = new User();
        user.setLastName(faker.name().lastName());
        assertThrows(PersistenceException.class, () -> saveAndFlush(user));
    }

    @Test
    void dateCreatedShouldBeImmutable() {
        String password = UUID.randomUUID().toString();

        User user = modelFactory.pipe(User.class)
                .then(it -> {
                    it.setPassword(password);
                    return it;
                })
                .create();

        LocalDateTime dateCreated = user.getDateCreated();
        user.setPassword(UUID.randomUUID().toString());
        saveAndFlush(user);
        entityManager.refresh(user);
        assertNotEquals(password, user.getPassword());
        assertEquals(dateCreated, user.getDateCreated());
    }
}