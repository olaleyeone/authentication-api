package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.data.enums.AuthenticationType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    private LocalDateTime lastActiveAt;
    private LocalDateTime becomesInactiveAt;
    private LocalDateTime autoLogoutAt;
    private LocalDateTime loggedOutAt;
    private LocalDateTime deactivatedAt;

    @Column(updatable = false, nullable = false)
    private LocalDateTime dateCreated;

    @PrePersist
    public void setPortalUser() {
        dateCreated = LocalDateTime.now();
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
