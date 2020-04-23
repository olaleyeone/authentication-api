package com.olalayeone.audittrailtest.data.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Store {

    @Id
    private Long id;
    @OneToMany
    private List<Item> items = new ArrayList<>();
    private Long itemsSold;
    private Boolean active;

    @ManyToOne
    private Location location;

    private String name;
}