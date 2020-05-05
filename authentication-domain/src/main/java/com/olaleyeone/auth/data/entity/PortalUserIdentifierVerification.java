package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.enums.UserIdentifierType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class PortalUserIdentifierVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserIdentifierType identifierType;

    @Column(nullable = false, unique = true)
    private String identifier;

    private String verificationCode;

    @Column(updatable = false, nullable = false)
    private String verificationCodeHash;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdOn;
    @Column(updatable = false, nullable = false)
    private LocalDateTime expiresOn;
    private LocalDateTime usedOn;

    @PrePersist
    public void prePersist() {
        if (createdOn != null) {
            return;
        }
        createdOn = LocalDateTime.now();
    }
}
