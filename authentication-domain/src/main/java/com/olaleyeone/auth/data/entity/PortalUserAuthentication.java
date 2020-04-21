package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.data.enums.AuthenticationType;
import com.olaleyeone.auth.data.embeddable.PersistTimeSetter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Data
public class PortalUserAuthentication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String identifier;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthenticationResponseType responseType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthenticationType type;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String userAgent;

    @ManyToOne
    private PortalUserIdentifier portalUserIdentifier;

    @ManyToOne
    private PortalUser portalUser;

    @Embedded
    @Delegate
    @Setter(value = AccessLevel.NONE)
    @Getter(value = AccessLevel.NONE)
    private PersistTimeSetter persistTimeSetter = new PersistTimeSetter();

    @PrePersist
    public void setPortalUser() {
        Optional.ofNullable(portalUserIdentifier)
                .map(PortalUserIdentifier::getPortalUser)
                .ifPresent(portalUser -> {
                    if (this.portalUser == null) {
                        this.portalUser = portalUser;
                    } else if (!this.portalUser.getId().equals(portalUser.getId())) {
                        throw new IllegalArgumentException();
                    }
                });
    }
}
