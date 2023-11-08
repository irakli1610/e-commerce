package com.code.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long Id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "country")
    private List<State> states;

}
