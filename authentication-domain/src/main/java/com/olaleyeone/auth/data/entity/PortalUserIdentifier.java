package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.data.shared.PersistTimeSetter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

import javax.persistence.*;

@Entity
@Data
public class PortalUserIdentifier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserIdentifierType identifierType;

    @Column(nullable = false, unique = true)
    private String identifier;

    private Boolean verified;

    @Embedded
    @Delegate
    @Setter(value = AccessLevel.NONE)
    @Getter(value = AccessLevel.NONE)
    private PersistTimeSetter persistTimeSetter = new PersistTimeSetter();

    @ManyToOne(optional = false)
    private PortalUser portalUser;
}
