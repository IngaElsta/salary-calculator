package com.github.ingaelsta.salarycalculator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Employee {
    @Id
    private Long id; //should be made into personal number?
    private String name;
    private String surname;
    private BigDecimal baseSalary;
    private Integer dependants;
    private Boolean useNonTaxableMinimum;
}
