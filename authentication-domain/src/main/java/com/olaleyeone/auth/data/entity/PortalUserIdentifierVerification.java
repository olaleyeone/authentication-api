package com.olaleyeone.auth.data.entity;

import com.olaleyeone.audittrail.api.IgnoreData;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import lombok.Data;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Data
@Entity
public class PortalUserIdentifierVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserIdentifierType identifierType;

    @Column(nullable = false)
    private String identifier;

    private String verificationCode;

    @IgnoreData
    @Column(updatable = false, nullable = false)
    private String verificationCodeHash;

    @Column(updatable = false, nullable = false)
    private OffsetDateTime createdAt;
    @Column(updatable = false, nullable = false)
    private OffsetDateTime expiresAt;

    private OffsetDateTime usedAt;
    private OffsetDateTime deactivatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt != null) {
            return;
        }
        createdAt = OffsetDateTime.now();
    }
}
