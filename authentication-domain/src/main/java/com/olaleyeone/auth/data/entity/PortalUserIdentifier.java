package com.olaleyeone.auth.data.entity;

import com.olaleyeone.audittrail.Audited;
import com.olaleyeone.audittrail.embeddable.Audit;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Delegate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class PortalUserIdentifier implements Audited {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserIdentifierType identifierType;

    @Column(nullable = false, unique = true)
    private String identifier;

    private LocalDateTime verifiedAt;
    private Boolean verified;

    @Delegate
    @Embedded
    @Setter(AccessLevel.NONE)
    private Audit audit = new Audit();

    @ManyToOne(optional = false)
    private PortalUser portalUser;

    @OneToOne
    private PortalUserIdentifierVerification verification;
}
