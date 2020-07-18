package com.olaleyeone.auth.data.entity.authentication;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class PortalUserAuthenticationData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private PortalUserAuthentication portalUserAuthentication;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition="TEXT")
    private String value;
}
