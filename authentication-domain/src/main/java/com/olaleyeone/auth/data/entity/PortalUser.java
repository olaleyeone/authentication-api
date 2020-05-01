package com.olaleyeone.auth.data.entity;

import com.olaleyeone.audittrail.Audited;
import com.olaleyeone.audittrail.api.IgnoreData;
import com.olaleyeone.audittrail.embeddable.Audit;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Delegate;

import javax.persistence.*;

@Entity
@Data
public class PortalUser implements Audited {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @IgnoreData
    private String password;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    private String otherName;

    @Delegate
    @Embedded
    @Setter(AccessLevel.NONE)
    private Audit audit = new Audit();
}
