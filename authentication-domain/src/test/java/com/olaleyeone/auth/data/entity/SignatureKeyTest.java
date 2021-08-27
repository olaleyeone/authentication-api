package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.test.entity.EntityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SignatureKeyTest extends EntityTest {

    private SignatureKey signatureKey;

    private PublicKey publicKey;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {

        signatureKey = new SignatureKey();
        signatureKey.setFormat(faker.animal().name());
        signatureKey.setKeyId(UUID.randomUUID().toString());
        signatureKey.setAlgorithm(faker.book().genre());
        signatureKey.setEncodedKey(faker.leagueOfLegends().quote().getBytes());
        signatureKey.setType(JwtTokenType.ACCESS);

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair keyPair = kpg.generateKeyPair();
        publicKey = keyPair.getPublic();
    }

    @Test
    void prePersist() {

        saveAndFlush(signatureKey);
        assertNotNull(signatureKey.getCreatedAt());
    }

    @Test
    void getRsaPublicKey() {
//        System.out.println("***********************");
//        System.out.println(new String(Base64.getEncoder().encode(publicKey.getEncoded())));
//        System.out.println("***********************");
        signatureKey.setAlgorithm(publicKey.getAlgorithm());
        signatureKey.setEncodedKey(publicKey.getEncoded());
        signatureKey.setFormat(publicKey.getFormat());

        RSAPublicKey rsaPublicKey = signatureKey.getRsaPublicKey();
        assertEquals(publicKey.getAlgorithm(), rsaPublicKey.getAlgorithm());
        assertEquals(publicKey.getFormat(), rsaPublicKey.getFormat());
        assertArrayEquals(publicKey.getEncoded(), rsaPublicKey.getEncoded());
    }

    @Test
    void getRsaPublicKey2() {
        signatureKey.setAlgorithm(publicKey.getAlgorithm());
        signatureKey.setEncodedKey(Base64.getDecoder().decode("MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAwoLRHEohDA/5vsSZg6AFVZqemdYRRU6MhlxQPdJWhOw6LzlG6lnWdsTK3spmsWI1KITKLH+se9uuG5bCbdkF3DAD565RjMb/jSWv9YpBgSg8MkBWUwBFAgxKQL1KIZfAjI3I8jEqeI8dvb6GPgA2b2NyNTPq928Czw4bafE2Z1zUDJXjS1qsjbBMlXarLqF2kwHvTRUIia/jr9ZbIb+hGoNJsjD6cXo8BUxjjPo9HtPJ4bZ/zGudGc6xjEv9lSKj+dXzfoeF8uQ9eCnq1ofMz1SQ43R7ID0WBrBvniQKdXyLTXvp3PhQ32NhP+pQPOIH+1zJCAWC6nSjqlEHRmtEOj9gB7g3EJheW1yTItLVipk6zrVRm8n2jBz1ZK1MXsCEsPsYwTRS8R6if3AXwXx16DeZOQfL04yoimtQxkS88GZ8kjIYrZEbcSlkxNgoFrNzEr83Ye7QMamVf/g5BhwH+KkcssXlXwtQ0jEHJn9rZAyEVbSUXpo/+cW3Rx1ctLuerDEt0OVYxU+8Gi+AmQ6S4zr3hWxRYqJofiJ+UHdm9EuTVvbr9HwLTDfCgoMAwHCGSuQLtumwWY51soflqSugmNCrz08NNgImGDzVyAiMC165HEX5vlOAY+n3UknNk1JMrD8hPUyCJvDgjNqBs8ZJlcsyomKlNwLU3OAtYfosc/MCAwEBAQ=="));
        signatureKey.setFormat(publicKey.getFormat());

        RSAPublicKey rsaPublicKey = signatureKey.getRsaPublicKey();
        assertEquals(publicKey.getAlgorithm(), rsaPublicKey.getAlgorithm());
//        assertEquals(publicKey.getFormat(), rsaPublicKey.getFormat());
//        assertArrayEquals(publicKey.getEncoded(), rsaPublicKey.getEncoded());
        System.out.println(Base64.getEncoder().encodeToString(rsaPublicKey.getPublicExponent().toByteArray()));
        System.out.println(Base64.getEncoder().encodeToString(rsaPublicKey.getModulus().toByteArray()));
    }

    @Test
    void getRsaPublicKeyWithBadData() {

        signatureKey.setAlgorithm(publicKey.getAlgorithm());
        signatureKey.setFormat(publicKey.getFormat());
        signatureKey.setEncodedKey(faker.leagueOfLegends().quote().getBytes());

        assertThrows(InvalidKeySpecException.class, () -> signatureKey.getRsaPublicKey());
    }
}