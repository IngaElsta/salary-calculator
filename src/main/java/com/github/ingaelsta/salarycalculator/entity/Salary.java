package com.github.ingaelsta.salarycalculator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Salary {
    private Long employeeId;
    private Integer month;
    private Integer year;
    private Double baseSalary;
    private Double calculatedNonTaxableSum;
    private Double calculatedIncomeTax;
    private Double calculatedSocialTax;
    private Double calculatedPayout;
}
