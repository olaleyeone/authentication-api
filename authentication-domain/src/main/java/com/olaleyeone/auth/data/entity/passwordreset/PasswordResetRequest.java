package com.olaleyeone.auth.data.entity.passwordreset;

import com.olaleyeone.audittrail.api.IgnoreData;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Entity
@Data
public class PasswordResetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(updatable = false)
    private PortalUserIdentifier portalUserIdentifier;

    @Setter(AccessLevel.NONE)
    @ManyToOne(optional = false)
    @JoinColumn(updatable = false)
    private PortalUser portalUser;

    @Column(nullable = false)
    private String resetCode;

    @IgnoreData
    @Column(updatable = false, nullable = false)
    private String resetCodeHash;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdOn;

    @Column(updatable = false, nullable = false)
    private LocalDateTime expiresOn;

    private LocalDateTime usedOn;
    private LocalDateTime deactivatedOn;

    @Transient
    public Instant getExpiryInstant() {
        return Optional.ofNullable(expiresOn)
                .map(it -> it.atZone(ZoneId.systemDefault()).toInstant())
                .orElse(null);
    }

    @Transient
    public Long getSecondsTillExpiry() {
        return Instant.now().until(getExpiryInstant(), ChronoUnit.SECONDS);
    }

    @PrePersist
    public void prePersist() {
        if (createdOn == null) {
            createdOn = LocalDateTime.now();
        }
        if (portalUserIdentifier != null) {
            portalUser = portalUserIdentifier.getPortalUser();
        }
    }
}
