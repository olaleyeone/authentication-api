package com.olaleyeone.audittrail.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Data
@Embeddable
public class Audit {

    private LocalDateTime createdOn;
    private String createdBy;

    private LocalDateTime updatedOn;
    private String  updatedBy;
}
