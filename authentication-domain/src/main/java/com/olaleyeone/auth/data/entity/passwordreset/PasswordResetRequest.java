package com.olaleyeone.auth.data.entity.passwordreset;

import com.olaleyeone.audittrail.api.IgnoreData;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.utils.TimeUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;

@Entity
@Data
public class PasswordResetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String userAgent;

    @ManyToOne(optional = false)
    @JoinColumn(updatable = false)
    private PortalUserIdentifier portalUserIdentifier;

    @Setter(AccessLevel.NONE)
    @ManyToOne(optional = false)
    @JoinColumn(updatable = false)
    private PortalUser portalUser;

    @Column
    private String resetCode;

    @IgnoreData
    @Column(updatable = false)
    private String resetCodeHash;

    @Column(updatable = false, nullable = false)
    private OffsetDateTime createdOn;

    @Column(updatable = false, nullable = false)
    private OffsetDateTime expiresOn;

    private OffsetDateTime usedOn;
    private OffsetDateTime deactivatedOn;

    private boolean autoLogin;

    @Transient
    public Instant getExpiryInstant() {
        return TimeUtil.toInstant(expiresOn);
    }

    @Transient
    public Long getSecondsTillExpiry() {
        return TimeUtil.secondsTill(getExpiryInstant());
    }

    @PrePersist
    public void prePersist() {
        if (createdOn == null) {
            createdOn = OffsetDateTime.now();
        }
        if (portalUserIdentifier != null) {
            portalUser = portalUserIdentifier.getPortalUser();
        }
    }
}
