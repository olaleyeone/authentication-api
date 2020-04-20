package com.olaleyeone.auth.data;

import com.google.gson.Gson;
import com.olaleyeone.auth.test.ComponentTest;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleJsonWebTokenTest extends ComponentTest {

    private Gson gson;
    private SimpleJsonWebToken simpleJsonWebToken;

    @Mock
    private Claims claims;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        simpleJsonWebToken = new SimpleJsonWebToken(claims, gson);
    }

    @Test
    public void getId() {
        String value = faker.lordOfTheRings().character();
        Mockito.doReturn(value).when(claims).getId();
        assertEquals(value, simpleJsonWebToken.getId());
    }

    @Test
    public void getIssuer() {
        String value = faker.lordOfTheRings().character();
        Mockito.doReturn(value).when(claims).getIssuer();
        assertEquals(value, simpleJsonWebToken.getIssuer());
    }

    @Test
    public void getSubject() {
        String value = faker.lordOfTheRings().character();
        Mockito.doReturn(value).when(claims).getSubject();
        assertEquals(value, simpleJsonWebToken.getSubject());
    }

    @Test
    public void getAudience() {
        List<String> value = Arrays.asList(faker.lordOfTheRings().character(), faker.lordOfTheRings().character());
        Mockito.doReturn(gson.toJson(value)).when(claims).getAudience();
        assertEquals(value, simpleJsonWebToken.getAudience());
    }

    @Test
    public void getExpirationTime() {
        Instant value = Instant.now();
        Mockito.doReturn(Date.from(value)).when(claims).getExpiration();
        assertEquals(Date.from(value), Date.from(simpleJsonWebToken.getExpirationTime()));
    }

    @Test
    public void getStartTime() {
        Instant value = Instant.now();
        Mockito.doReturn(Date.from(value)).when(claims).getNotBefore();
        assertEquals(Date.from(value), Date.from(simpleJsonWebToken.getStartTime()));
    }

    @Test
    public void getTimeIssued() {
        Instant value = Instant.now();
        Mockito.doReturn(Date.from(value)).when(claims).getIssuedAt();
        assertEquals(Date.from(value), Date.from(simpleJsonWebToken.getTimeIssued()));
    }
}