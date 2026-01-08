package com.java.dnc.school_manager.model;

import jakarta.persistence.*;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class Person {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    String cpf;
    @Column(nullable = false)
    String email;
    @Column(nullable = false)
    String phoneNumber;

    @Embedded
    private Address address;

}
