package com.github.ingaelsta.salarycalculator.controller;

import com.github.ingaelsta.salarycalculator.entity.Salary;
import com.github.ingaelsta.salarycalculator.service.SalaryCalculatorService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("salary-calculations")
public class SalaryCalculatorController {
    private final SalaryCalculatorService salaryCalculatorService;

    public SalaryCalculatorController(SalaryCalculatorService salaryCalculatorService) {
        this.salaryCalculatorService = salaryCalculatorService;
    }

    @GetMapping
    public Salary getSalary(
            @RequestParam(value = "id") @NotNull Long employeeId,
            @RequestParam(value = "month") @NotNull @Min(1) @Max(12) Integer month,
            @RequestParam(value = "year") @NotNull @Min(2022) Integer year) {
        return salaryCalculatorService.getSalary(employeeId, month, year);
    }
}
