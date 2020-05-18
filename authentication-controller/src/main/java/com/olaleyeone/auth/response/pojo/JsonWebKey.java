package com.olaleyeone.auth.response.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JsonWebKey {

    public static final String SIGNATURE_USE = "sig";

    private String kid;
    private String alg;
    private String kty;
    private String use = SIGNATURE_USE;
    private String modulus;
    private String exponent;

    private LocalDateTime createdOn;
}
