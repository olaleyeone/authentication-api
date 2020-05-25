package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.entitytest.EntityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
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
        assertNotNull(signatureKey.getCreatedOn());
    }

    @Test
    void getRsaPublicKey() {

        signatureKey.setAlgorithm(publicKey.getAlgorithm());
        signatureKey.setEncodedKey(publicKey.getEncoded());
        signatureKey.setFormat(publicKey.getFormat());

        RSAPublicKey rsaPublicKey = signatureKey.getRsaPublicKey();
        assertEquals(publicKey.getAlgorithm(), rsaPublicKey.getAlgorithm());
        assertEquals(publicKey.getFormat(), rsaPublicKey.getFormat());
        assertArrayEquals(publicKey.getEncoded(), rsaPublicKey.getEncoded());
    }

    @Test
    void getRsaPublicKeyWithBadData() {

        signatureKey.setAlgorithm(publicKey.getAlgorithm());
        signatureKey.setFormat(publicKey.getFormat());
        signatureKey.setEncodedKey(faker.leagueOfLegends().quote().getBytes());

        assertThrows(InvalidKeySpecException.class, () -> signatureKey.getRsaPublicKey());
    }
}