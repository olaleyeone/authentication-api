package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.data.shared.PersistTimeSetter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

import javax.persistence.*;

@Entity
@Data
public class AuthenticationResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String identifier;

    @Column(nullable = false)
    private AuthenticationResponseType authenticationResponseType;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String userAgent;

    @ManyToOne
    private UserIdentifier userIdentifier;

    @Embedded
    @Delegate
    @Setter(value = AccessLevel.NONE)
    @Getter(value = AccessLevel.NONE)
    private PersistTimeSetter persistTimeSetter = new PersistTimeSetter();
}
