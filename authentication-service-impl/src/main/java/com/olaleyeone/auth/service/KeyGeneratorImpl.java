package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.repository.SignatureKeyRepository;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.security.*;
import java.util.Map;
import java.util.UUID;

@Named
public class KeyGeneratorImpl implements KeyGenerator {

    private final KeyPairGenerator kpg;

    @Inject
    private SignatureKeyRepository signatureKeyRepository;

    public KeyGeneratorImpl() throws NoSuchAlgorithmException {
        kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
    }

    @Activity("GENERATE SIGNATURE KEY")
    @Transactional
    @Override
    public Map.Entry<Key, SignatureKey> generateKey(JwtTokenType jwtTokenType) {
        KeyPair keyPair = kpg.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        SignatureKey signatureKey = new SignatureKey();
        signatureKey.setAlgorithm(publicKey.getAlgorithm());
        signatureKey.setEncodedKey(publicKey.getEncoded());
        signatureKey.setFormat(publicKey.getFormat());
        signatureKey.setKeyId(UUID.randomUUID().toString());
        signatureKey.setType(jwtTokenType);
        signatureKeyRepository.save(signatureKey);
        return Pair.of(privateKey, signatureKey);
    }
}
