package com.github.ingaelsta.salarycalculator.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
public class Constant {
    @Id
    private Long id;
    private String name;
    private Double val;
    private LocalDate startDate;
    private LocalDate endDate;
}
