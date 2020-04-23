package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.embeddable.PersistTimeSetter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
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
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private PortalUserAuthentication actualAuthentication;

    @ManyToOne(optional = false)
    @Setter(value = AccessLevel.NONE)
    private PortalUser portalUser;

    @Embedded
    @Delegate
    @Setter(value = AccessLevel.NONE)
    @Getter(value = AccessLevel.NONE)
    private PersistTimeSetter persistTimeSetter = new PersistTimeSetter();

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
    public Instant getAccessExpiryInstant() {
        return Optional.ofNullable(accessExpiresAt)
                .map(it -> it.atZone(ZoneId.systemDefault()).toInstant())
                .orElse(null);
    }

    @Transient
    public Long getSecondsTillExpiry() {
        return Instant.now().until(getExpiryInstant(), ChronoUnit.SECONDS);
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
