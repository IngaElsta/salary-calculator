package com.github.ingaelsta.salarycalculator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Employee {
    @Id
    private Long id; //should be made into personal number?
    private String name;
    private String surname;
    private Double baseSalary;
    private Integer dependents;
    private Boolean useNonTaxableMinimum;
}
