package com.olaleyeone.auth.data.entity;

import com.olaleyeone.audittrail.embeddable.Audit;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Delegate;

import javax.persistence.*;

@Data
@Entity
public class PortalUserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Delegate
    @Embedded
    @Setter(AccessLevel.NONE)
    private Audit audit = new Audit();

    @ManyToOne(optional = false)
    private PortalUser portalUser;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition="TEXT")
    private String value;
}
