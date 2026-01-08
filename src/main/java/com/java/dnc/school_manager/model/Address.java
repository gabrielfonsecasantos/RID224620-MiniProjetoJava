package com.java.dnc.school_manager.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.SecondaryTable;
import lombok.Data;

@Embeddable
@Data
public class Address {
    private String cep;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String uf;
}
