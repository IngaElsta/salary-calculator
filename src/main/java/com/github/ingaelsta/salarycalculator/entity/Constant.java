package com.github.ingaelsta.salarycalculator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Constant implements Comparable <Constant>{
    public static final String MAX_NON_TAXABLE_MINIMUM = "MAX_NON_TAXABLE_MINIMUM";
    public static final String NON_TAXABLE_LOWER_BOUND = "NON_TAXABLE_LOWER_BOUND";
    public static final String NON_TAXABLE_UPPER_BOUND = "NON_TAXABLE_UPPER_BOUND";
    public static final String NON_TAXABLE_AMOUNT_FOR_EACH_DEPENDANT = "NON_TAXABLE_AMOUNT_FOR_EACH_DEPENDANT";
    public static final String INCOME_TAX_RATE = "INCOME_TAX_RATE";
    public static final String SOCIAL_TAX_RATE = "SOCIAL_TAX_RATE";

    @Id
    private Long id;
    private String name;
    private Double val;
    private LocalDate startDate;
    private LocalDate endDate;


    @Override
    public int compareTo(Constant constant) {
        return startDate.compareTo(constant.startDate);
    }
}
