package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.repository.SignatureKeyRepository;
import com.olaleyeone.auth.response.pojo.JsonWebKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SignatureKeyControllerTest extends ControllerTest {

    @Autowired
    private SignatureKeyRepository signatureKeyRepository;

    private SignatureKey signatureKey;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        signatureKey = new SignatureKey();
        signatureKey.setFormat(faker.animal().name());
        signatureKey.setKeyId(UUID.randomUUID().toString());

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair keyPair = kpg.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        signatureKey.setAlgorithm(publicKey.getAlgorithm());
        signatureKey.setEncodedKey(publicKey.getEncoded());
    }

    @Test
    void getJsonWebKey() throws Exception {
        Mockito.doReturn(Optional.of(signatureKey)).when(signatureKeyRepository).findByKeyIdAndType(Mockito.any(), Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.get("/keys/{kid}", signatureKey.getKeyId()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    JsonWebKey response = objectMapper.readValue(result.getResponse().getContentAsByteArray(), JsonWebKey.class);
                    assertNotNull(response);
                    assertEquals(signatureKey.getKeyId(), response.getKid());
                    assertEquals(signatureKey.getAlgorithm(), response.getKty());
                    assertEquals("sig", response.getUse());
                    RSAPublicKey rsaPublicKey = signatureKey.getRsaPublicKey();
                    Base64.Encoder encoder = Base64.getEncoder();
                    assertEquals(encoder.encodeToString(rsaPublicKey.getModulus().toByteArray()), response.getModulus());
                    assertEquals(encoder.encodeToString(rsaPublicKey.getPublicExponent().toByteArray()), response.getExponent());
                });
        Mockito.verify(signatureKeyRepository, Mockito.times(1))
                .findByKeyIdAndType(signatureKey.getKeyId(), JwtTokenType.ACCESS);
    }
}