package com.olaleyeone.auth.response.pojo;

import com.olaleyeone.auth.data.entity.SignatureKey;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.util.Base64;

@Data
@NoArgsConstructor
public class JsonWebKey {

    public static final String SIGNATURE_USE = "sig";

    private String kid;
    private String alg;
    private String kty;
    private String use = SIGNATURE_USE;
    private String modulus;
    private String exponent;

    private LocalDateTime createdOn;

    public JsonWebKey(SignatureKey signatureKey) {
        this.kid = signatureKey.getKeyId();
        this.kty = signatureKey.getAlgorithm();
        this.use = JsonWebKey.SIGNATURE_USE;
        RSAPublicKey rsaPublicKey = signatureKey.getRsaPublicKey();
        Base64.Encoder encoder = Base64.getEncoder();
        this.exponent = encoder.encodeToString(rsaPublicKey.getPublicExponent().toByteArray());
        this.modulus = encoder.encodeToString(rsaPublicKey.getModulus().toByteArray());

        this.createdOn = signatureKey.getCreatedOn();
    }
}
