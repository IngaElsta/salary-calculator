package com.github.ingaelsta.salarycalculator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private String name;
    @NotNull
    private String surname;
    @NotNull
    private BigDecimal baseSalary;
    @NotNull
    private Integer dependants;
    @NotNull
    private Boolean useNonTaxableMinimum;
}
