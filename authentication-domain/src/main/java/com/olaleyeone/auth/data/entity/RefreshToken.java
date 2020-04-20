package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.shared.PersistTimeSetter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    private LocalDateTime expiresAt;

    private LocalDateTime timeDeactivated;

    @PrePersist
    public void setPortalUser() {
        portalUser = Optional.ofNullable(actualAuthentication)
                .map(PortalUserAuthentication::getPortalUser)
                .orElse(null);
    }
}
