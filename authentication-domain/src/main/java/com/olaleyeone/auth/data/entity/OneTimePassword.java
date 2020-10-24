package com.olaleyeone.auth.data.entity;

import com.olaleyeone.audittrail.api.IgnoreData;
import lombok.Data;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Data
@Entity
public class OneTimePassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private PortalUserIdentifier userIdentifier;

    private String password;

    @IgnoreData
    @Column(updatable = false, nullable = false)
    private String hash;

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
