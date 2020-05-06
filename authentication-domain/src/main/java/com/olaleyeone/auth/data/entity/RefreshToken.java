package com.olaleyeone.auth.data.entity;

import com.olaleyeone.audittrail.Audited;
import com.olaleyeone.audittrail.embeddable.Audit;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Delegate;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Entity
@Data
public class RefreshToken implements Audited {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private PortalUserAuthentication actualAuthentication;

    @ManyToOne(optional = false)
    @Setter(value = AccessLevel.NONE)
    private PortalUser portalUser;

    @Delegate
    @Embedded
    @Setter(AccessLevel.NONE)
    private Audit audit = new Audit();

    @Column(nullable = false, updatable = false)
    private LocalDateTime accessExpiresAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime lastUsedAt;
    private LocalDateTime timeDeactivated;

    @Transient
    public Instant getExpiryInstant() {
        return Optional.ofNullable(expiresAt)
                .map(it -> it.atZone(ZoneId.systemDefault()).toInstant())
                .orElse(null);
    }

    @Transient
    public Long getSecondsTillExpiry() {
        return Instant.now().until(getExpiryInstant(), ChronoUnit.SECONDS);
    }

    @Transient
    public Instant getAccessExpiryInstant() {
        return Optional.ofNullable(accessExpiresAt)
                .map(it -> it.atZone(ZoneId.systemDefault()).toInstant())
                .orElse(null);
    }

    @Transient
    public Long getSecondsTillAccessExpiry() {
        return Instant.now().until(getAccessExpiryInstant(), ChronoUnit.SECONDS);
    }

    @PrePersist
    public void setPortalUser() {
        portalUser = Optional.ofNullable(actualAuthentication)
                .map(PortalUserAuthentication::getPortalUser)
                .orElse(null);
    }
}
