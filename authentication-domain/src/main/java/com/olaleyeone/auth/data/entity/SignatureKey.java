package com.olaleyeone.auth.data.entity;

import com.olaleyeone.audittrail.api.IgnoreData;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import lombok.Data;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;

@Data
@Entity
public class SignatureKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String keyId;

    @IgnoreData
    @Column(nullable = false, columnDefinition = "TEXT")
    private String encodedKey;

    @Column(nullable = false)
    private String algorithm;

    @Column(nullable = false)
    private String format;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private JwtTokenType type;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public void setEncodedKey(byte[] data) {
        encodedKey = Base64.getEncoder().encodeToString(data);
    }

    public byte[] getRawEncodedKey() {
        return Base64.getDecoder().decode(encodedKey);
    }

    @SneakyThrows
    public RSAPublicKey getRsaPublicKey() {
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec pubKs = new X509EncodedKeySpec(getRawEncodedKey());
        return (RSAPublicKey) kf.generatePublic(pubKs);
    }
}
