package com.github.ingaelsta.salarycalculator.service;

import com.github.ingaelsta.salarycalculator.commons.repositories.TestConstantRepository;
import com.github.ingaelsta.salarycalculator.commons.repositories.TestEmployeeRepository;
import com.github.ingaelsta.salarycalculator.entity.Employee;
import com.github.ingaelsta.salarycalculator.entity.Salary;
import com.github.ingaelsta.salarycalculator.repository.ConstantRepository;
import com.github.ingaelsta.salarycalculator.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class SalaryCalculatorServiceTest {

    private final ConstantRepository constantRepository = new TestConstantRepository();
    private final EmployeeRepository employeeRepositoryMock = new TestEmployeeRepository();
    private SalaryCalculatorService salaryCalculatorService;

    private final RoundingMode halfUp = RoundingMode.HALF_UP;


    @BeforeEach
    void setUp() {
        salaryCalculatorService = new SalaryCalculatorService(constantRepository, employeeRepositoryMock);
    }

    @Test
    void When_salaryBelowLowerNonTaxableMinimumBound_then_fullNonTaxableMinimumApplied(){
        Long employeeId = 1L;
        Employee employee = employeeRepositoryMock.findById(employeeId).get();
        Integer month = 3;
        Integer year = 2023;
        Salary expected = new Salary(1L,
                month,
                year,
                employee.getBaseSalary().setScale(2, halfUp),
                BigDecimal.valueOf(358.0).setScale(2, halfUp),
                BigDecimal.ZERO.setScale(2, halfUp),
                BigDecimal.valueOf(42.00).setScale(2, halfUp),
                BigDecimal.valueOf(358.0).setScale(2, halfUp)
                );
        assertEquals(expected, salaryCalculatorService.getSalary(employeeId, month, year));
    }

}