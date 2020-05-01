package com.olaleyeone.auth.data.entity;

import com.olaleyeone.audittrail.api.IgnoreData;
import lombok.Data;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;

@Data
@Entity
public class SignatureKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String keyId;

    @IgnoreData
    @Lob
    @Column(nullable = false)
    private byte[] encodedKey;

    @Column(nullable = false)
    private String algorithm;

    @Column(nullable = false)
    private String format;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdOn;

    @PrePersist
    public void prePersist() {
        createdOn = LocalDateTime.now();
    }

    @SneakyThrows
    public RSAPublicKey getRsaPublicKey() {
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec pubKs = new X509EncodedKeySpec(encodedKey);
        return (RSAPublicKey) kf.generatePublic(pubKs);
    }
}
