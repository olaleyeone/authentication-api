package com.olaleyeone.audittrail.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class UnitOfWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private RequestLog request;

    @Column(updatable = false, nullable = false)
    private Long estimatedTimeTakenInNanos;

    @Column(updatable = false, nullable = false)
    private LocalDateTime completedOn;

    @PrePersist
    public void setCompletedOn() {
        completedOn = LocalDateTime.now();
    }
}
