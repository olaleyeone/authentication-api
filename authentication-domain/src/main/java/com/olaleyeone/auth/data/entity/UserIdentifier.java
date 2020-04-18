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
public class UserIdentifier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserIdentifierType identifierType;

    @Column(nullable = false, unique = true)
    private String identifier;

    @Embedded
    @Delegate
    @Setter(value = AccessLevel.NONE)
    @Getter(value = AccessLevel.NONE)
    private PersistTimeSetter persistTimeSetter = new PersistTimeSetter();

    @ManyToOne(optional = false)
    private User user;
}
