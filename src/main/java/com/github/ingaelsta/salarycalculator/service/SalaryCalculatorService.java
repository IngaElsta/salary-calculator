package com.github.ingaelsta.salarycalculator.service;

import com.github.ingaelsta.salarycalculator.entity.Constant;
import com.github.ingaelsta.salarycalculator.entity.Constants;
import com.github.ingaelsta.salarycalculator.entity.Employee;
import com.github.ingaelsta.salarycalculator.entity.Salary;
import com.github.ingaelsta.salarycalculator.repository.ConstantRepository;
import com.github.ingaelsta.salarycalculator.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.apache.commons.math3.util.Precision;

import java.util.List;

@Service
public class SalaryCalculatorService {

    private final ConstantRepository constantRepository;
    private final EmployeeRepository employeeRepository;

    public SalaryCalculatorService(ConstantRepository constantRepository, EmployeeRepository employeeRepository) {
        this.constantRepository = constantRepository;
        this.employeeRepository = employeeRepository;
    }

    public Salary getSalary(Long employeeId, Integer month, Integer year) {
        //todo: implement error handling
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException(String.format("Employee with id %d not found", employeeId)));
        Double socialTax = getConstant(Constants.SOCIAL_TAX, month, year);

        //social tax
        double taxableAmount = employee.getBaseSalary();
        double calculatedSocialTax = Precision.round(taxableAmount * (socialTax), 2);
        taxableAmount -= calculatedSocialTax;

        double calculatedNonTaxableSum = calculateNonTaxableSum(employee.getBaseSalary(), taxableAmount, employee.getDependants(), employee.getUseNonTaxableMinimum(), month, year);
        double calculatedPayout = taxableAmount;
        taxableAmount = Math.max(taxableAmount - calculatedNonTaxableSum, 0);

        //todo: implement differentiated income tax
        Double incomeTax = getConstant(Constants.INCOME_TAX, month, year);
        double calculatedIncomeTax = Precision.round(taxableAmount * incomeTax, 2);
        calculatedPayout = Precision.round(calculatedPayout - calculatedIncomeTax, 2);

        return new Salary(employeeId,
                month, year,
                employee.getBaseSalary(),
                calculatedNonTaxableSum,
                calculatedIncomeTax,
                calculatedSocialTax,
                calculatedPayout);
    }

    protected Double calculateNonTaxableSum (
            Double baseSalary,
            Double taxableAmount,
            Integer dependants,
            Boolean useNonTaxableMinimum,
            int month,
            int year) {
        double nonTaxableAmount = 0;
        if ((dependants != null) && (dependants > 0)) {
            Double nonTaxableForDependants = getConstant(Constants.NON_TAXABLE_DEPENDANT, month, year);
            nonTaxableAmount += nonTaxableForDependants * dependants;
        }

        if (useNonTaxableMinimum == null || !useNonTaxableMinimum) {
            return Math.min(taxableAmount, nonTaxableAmount);
        }

        Double nonTaxableUpper = getConstant(Constants.NON_TAXABLE_UPPER, month, year);
        if (taxableAmount > nonTaxableUpper) {
            return Math.min(taxableAmount, nonTaxableAmount);
        }

        Double nonTaxableMinimum = getConstant(Constants.NON_TAXABLE_MINIMUM, month, year);
        Double nonTaxableLower = getConstant(Constants.NON_TAXABLE_LOWER, month, year);
        if (taxableAmount <= nonTaxableLower) {
            nonTaxableAmount += Math.min(taxableAmount, nonTaxableMinimum);
            return Math.min(taxableAmount, nonTaxableAmount);
        }
        double coefficient = nonTaxableMinimum / (nonTaxableUpper - nonTaxableLower);
//        K = GNMmax / (AImax – AImin);
        double diffNonTaxableAmount = nonTaxableMinimum - coefficient * (baseSalary - nonTaxableLower);
        nonTaxableAmount = Precision.round( Math.min(diffNonTaxableAmount + nonTaxableAmount, taxableAmount), 2);
//        GDNM = GNMmax – K x (AI – AImin)
        return Math.min(taxableAmount, nonTaxableAmount);
    }

    private Double getConstant(String constantName, Integer month, Integer year) {
        //todo: implement check for correct period
        List<Constant> constantList = constantRepository.findByNameOrderByStartDateDesc(constantName);
        if (constantList.isEmpty()) {
            String prettyName = constantName.toLowerCase().replace("_", " ");
            throw new RuntimeException(String.format("No value found for %s", prettyName));
        }
        Constant constant = constantList.get(0);
        return constant.getVal();
    }
}
