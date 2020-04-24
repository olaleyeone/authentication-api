package com.olaleyeone.audittrail.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private UnitOfWork unitOfWork;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String className;
    @Column(nullable = false)
    private String methodName;
    @Column(nullable = false)
    private Integer lineNumber;

    @Column(nullable = false)
    private Integer precedence;
}
