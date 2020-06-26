package com.olaleyeone.auth.data.entity.passwordreset;

import com.olaleyeone.audittrail.api.IgnoreData;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class PasswordResetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private PortalUserIdentifier portalUserIdentifier;

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

    @PrePersist
    public void prePersist() {
        if (createdOn != null) {
            return;
        }
        createdOn = LocalDateTime.now();
    }
}
