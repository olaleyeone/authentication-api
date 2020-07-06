package com.olaleyeone.auth.data.entity;

import com.olaleyeone.audittrail.Audited;
import com.olaleyeone.audittrail.embeddable.Audit;
import com.olaleyeone.auth.data.enums.Gender;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Delegate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Data
public class PortalUser implements Audited {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @IgnoreData
    private String password;

    private Boolean passwordUpdateRequired;

    private OffsetDateTime passwordLastUpdatedOn;

    private String displayName;

    private String firstName;

    private String lastName;

    private String otherName;

    private Gender gender;

    @Delegate
    @Embedded
    @Setter(AccessLevel.NONE)
    private Audit audit = new Audit();

    private LocalDateTime publishedOn;
}
