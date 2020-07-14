package com.olaleyeone.auth.data.entity.authentication;

import com.olaleyeone.audittrail.embeddable.Audit;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Delegate;

import javax.persistence.*;

@Data
@Entity
public class PortalUserAuthenticationData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Delegate
    @Embedded
    @Setter(AccessLevel.NONE)
    private Audit audit = new Audit();

    @ManyToOne(optional = false)
    private PortalUserAuthentication portalUserAuthentication;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition="TEXT")
    private String value;
}
