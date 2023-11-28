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
