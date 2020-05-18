package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.repository.SignatureKeyRepository;
import com.olaleyeone.auth.response.pojo.JsonWebKey;
import com.olaleyeone.auth.security.annotations.Public;
import com.olaleyeone.rest.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

@RequiredArgsConstructor
@RestController
public class SignatureKeyController {

    private final SignatureKeyRepository signatureKeyRepository;

    @Public
    @GetMapping("/keys/{kid}")
    public JsonWebKey getJsonWebKey(@PathVariable("kid") String kid) {
        return signatureKeyRepository.findByKeyId(kid)
                .map(signatureKey -> {
                    JsonWebKey jsonWebKey = new JsonWebKey();
                    jsonWebKey.setKid(signatureKey.getKeyId());
//                    jsonWebKey.setAlg(signatureKey.getAlgorithm());
                    jsonWebKey.setKty(signatureKey.getAlgorithm());
                    jsonWebKey.setUse(JsonWebKey.SIGNATURE_USE);
                    RSAPublicKey rsaPublicKey = signatureKey.getRsaPublicKey();
                    Base64.Encoder encoder = Base64.getEncoder();
                    jsonWebKey.setExponent(encoder.encodeToString(rsaPublicKey.getPublicExponent().toByteArray()));
                    jsonWebKey.setModulus(encoder.encodeToString(rsaPublicKey.getModulus().toByteArray()));

                    jsonWebKey.setCreatedOn(signatureKey.getCreatedOn());
                    return jsonWebKey;
                }).orElseThrow(NotFoundException::new);
    }
}
