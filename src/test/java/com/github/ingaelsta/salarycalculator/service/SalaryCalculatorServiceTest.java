package com.github.ingaelsta.salarycalculator.service;

import com.github.ingaelsta.salarycalculator.commons.repositories.TestConstantRepository;
import com.github.ingaelsta.salarycalculator.commons.repositories.TestEmployeeRepository;
import com.github.ingaelsta.salarycalculator.entity.Constant;
import com.github.ingaelsta.salarycalculator.entity.Employee;
import com.github.ingaelsta.salarycalculator.entity.Salary;
import com.github.ingaelsta.salarycalculator.exceptions.ConstantValueMissingException;
import com.github.ingaelsta.salarycalculator.exceptions.EmployeeNotFoundException;
import com.github.ingaelsta.salarycalculator.repository.ConstantRepository;
import com.github.ingaelsta.salarycalculator.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

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
    void When_salaryBelowLowerNonTaxableMinimumBound_then_noIncomeTaxApplied(){
        Long employeeId = 1L;
        Employee employee = employeeRepositoryMock.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Test setup is incomplete"));;
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
        Salary actual = salaryCalculatorService.getSalary(employeeId, month, year);
        assertEquals(actual.getCalculatedIncomeTax(), BigDecimal.ZERO.setScale(2, halfUp));
        assertEquals(expected, actual);
    }

    @Test
    void When_useNonTaxableMinimumIsFalse_then_nonTaxableMinimumNotApplied(){
        Long employeeId = 2L;
        Employee employee = employeeRepositoryMock.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Test setup is incomplete"));
        Integer month = 3;
        Integer year = 2023;
        Salary expected = new Salary(2L,
                month,
                year,
                employee.getBaseSalary().setScale(2, halfUp),
                BigDecimal.ZERO.setScale(2, halfUp),
                BigDecimal.valueOf(71.60).setScale(2, halfUp),
                BigDecimal.valueOf(42.00).setScale(2, halfUp),
                BigDecimal.valueOf(286.40).setScale(2, halfUp)
        );
        Salary actual = salaryCalculatorService.getSalary(employeeId, month, year);
        assertEquals(actual.getCalculatedNonTaxableSum(), BigDecimal.ZERO.setScale(2, halfUp));
        assertEquals(expected, actual);
    }

    @Test
    void When_useNonTaxableMinimumIsFalseAndThereAreDependants_then_nonTaxableForDependantsIsApplied(){
        Long employeeId = 3L;
        Employee employee = employeeRepositoryMock.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Test setup is incomplete"));;
        Integer month = 3;
        Integer year = 2023;
        Salary expected = new Salary(3L,
                month,
                year,
                employee.getBaseSalary().setScale(2, halfUp),
                BigDecimal.valueOf(250).setScale(2, halfUp),
                BigDecimal.valueOf(21.60).setScale(2, halfUp),
                BigDecimal.valueOf(42.00).setScale(2, halfUp),
                BigDecimal.valueOf(336.40).setScale(2, halfUp)
        );
        Salary actual = salaryCalculatorService.getSalary(employeeId, month, year);
        assertEquals(actual.getCalculatedNonTaxableSum(), BigDecimal.valueOf(250).setScale(2, halfUp));
        assertEquals(expected, actual);
    }

    @Test
    void When_salaryBetweenPartialNonTaxableMinimumLowerBoundAndUpperBound_then_PartialNonTaxableMinimumApplied(){
        Long employeeId = 4L;
        Employee employee = employeeRepositoryMock.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Test setup is incomplete"));;
        Integer month = 3;
        Integer year = 2023;
        Salary expected = new Salary(4L,
                month,
                year,
                employee.getBaseSalary().setScale(2, halfUp),
                BigDecimal.valueOf(153.85).setScale(2, halfUp),
                BigDecimal.valueOf(219.83).setScale(2, halfUp),
                BigDecimal.valueOf(147.00).setScale(2, halfUp),
                BigDecimal.valueOf(1033.17).setScale(2, halfUp)
        );
        Salary actual = salaryCalculatorService.getSalary(employeeId, month, year);
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    void When_salaryBelowPartialNonTaxableMinimumUpperBoundAndMidTaxBracket_then_nonTaxableMinimumIsNotApplied(){
        Long employeeId = 5L;
        Employee employee = employeeRepositoryMock.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Test setup is incomplete"));;
        Integer month = 3;
        Integer year = 2023;
        Salary expected = new Salary(5L,
                month,
                year,
                employee.getBaseSalary().setScale(2, halfUp),
                BigDecimal.valueOf(38.46).setScale(2, halfUp),
                BigDecimal.valueOf(297.60).setScale(2, halfUp),
                BigDecimal.valueOf(178.50).setScale(2, halfUp),
                BigDecimal.valueOf(1223.90).setScale(2, halfUp)
        );
        Salary actual = salaryCalculatorService.getSalary(employeeId, month, year);
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    void When_salaryAboveMinimalTaxBracket_then_nonTaxableMinimumIsNotApplied(){
        Long employeeIdUsesMinimumTrue = 6L;
        Long employeeIdUsesMinimumFalse = 7L;
        Employee employeeUseMinimumTrue  = employeeRepositoryMock.findById(employeeIdUsesMinimumTrue)
                .orElseThrow(() -> new RuntimeException("Test setup is incomplete"));
        Integer month = 3;
        Integer year = 2023;
        Salary expected = new Salary(6L,
                month,
                year,
                employeeUseMinimumTrue .getBaseSalary().setScale(2, halfUp),
                BigDecimal.valueOf(0).setScale(2, halfUp),
                BigDecimal.valueOf(347.09).setScale(2, halfUp),
                BigDecimal.valueOf(199.50).setScale(2, halfUp),
                BigDecimal.valueOf(1353.41).setScale(2, halfUp)
        );
        Salary actualWhenUseMinimumTrue = salaryCalculatorService.getSalary(employeeIdUsesMinimumTrue, month, year);
        Salary actualWhenUseMinimumFalse = salaryCalculatorService.getSalary(employeeIdUsesMinimumFalse, month, year);
        assertEquals(actualWhenUseMinimumTrue.getCalculatedNonTaxableSum(), actualWhenUseMinimumFalse.getCalculatedNonTaxableSum());
//        todo: implement income tax gradation
        assertEquals(expected, actualWhenUseMinimumTrue);
    }


    @Test
    void When_noEmployeeWithGivenIdFound_then_throwsEmployeeNotFoundException(){
        Long employeeId = 0L;
        Integer month = 3;
        Integer year = 2023;
        assertThrows(EmployeeNotFoundException.class,
                () -> salaryCalculatorService.getSalary(employeeId, month, year));
    }

    @Test
    void When_noConstantWithGivenName_then_throwsEmployeeNotFoundException(){
        Long employeeId = 1L;
        Integer month = 3;
        Integer year = 2023;
        constantRepository.deleteById(3L);
        assertThrows(ConstantValueMissingException.class,
                () -> salaryCalculatorService.getSalary(employeeId, month, year));
    }



}