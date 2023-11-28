package com.github.ingaelsta.salarycalculator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Salary {
    private Long employeeId;
    private Integer month;
    private Integer year;
    private BigDecimal baseSalary;
    private BigDecimal calculatedNonTaxableSum;
    private BigDecimal calculatedIncomeTax;
    private BigDecimal calculatedSocialTax;
    private BigDecimal calculatedPayout;
}
