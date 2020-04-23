package com.olaleyeone.audittrail.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String ipAddress;

    @Column
    private String userAgent;

    private String sessionId;
    private String userId;

    private String uri;
    private Integer statusCode;
    private Long estimatedTimeTakenInNanos;

    @Column(updatable = false, nullable = false)
    private LocalDateTime dateCreated;

    @PrePersist
    public void setDateCreated() {
        dateCreated = LocalDateTime.now();
    }
}
