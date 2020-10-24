package com.olaleyeone.auth.data.entity.authentication;

import com.olaleyeone.audittrail.Audited;
import com.olaleyeone.audittrail.embeddable.Audit;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.utils.TimeUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Delegate;

import javax.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;
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
    private OffsetDateTime accessExpiresAt;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime expiresAt;

    private OffsetDateTime deactivatedAt;

    public void setActualAuthentication(PortalUserAuthentication actualAuthentication) {
        this.actualAuthentication = actualAuthentication;
        this.portalUser = Optional.ofNullable(this.actualAuthentication)
                .map(PortalUserAuthentication::getPortalUser)
                .orElse(null);
    }

    @Transient
    public Instant getExpiryInstant() {
        return TimeUtil.toInstant(expiresAt);
    }

    @Transient
    public Long getSecondsTillExpiry() {
        return TimeUtil.secondsTill(getExpiryInstant());
    }

    @Transient
    public Instant getAccessExpiryInstant() {
        return TimeUtil.toInstant(accessExpiresAt);
    }

    @Transient
    public Long getSecondsTillAccessExpiry() {
        return TimeUtil.secondsTill(getAccessExpiryInstant());
    }

    @PrePersist
    public void beforePersist() {
        portalUser = Optional.ofNullable(actualAuthentication)
                .map(PortalUserAuthentication::getPortalUser)
                .orElse(null);
    }
}
