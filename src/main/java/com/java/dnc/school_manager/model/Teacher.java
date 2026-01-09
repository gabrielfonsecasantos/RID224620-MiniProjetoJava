package com.java.dnc.school_manager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "teachers")
@EqualsAndHashCode(callSuper = true)
public class Teacher extends Person{
    private String subject;
    private LocalDate hiringDate;
}
