package com.github.ingaelsta.salarycalculator.service;

import com.github.ingaelsta.salarycalculator.entity.Constant;
import com.github.ingaelsta.salarycalculator.entity.Constants;
import com.github.ingaelsta.salarycalculator.entity.Employee;
import com.github.ingaelsta.salarycalculator.entity.Salary;
import com.github.ingaelsta.salarycalculator.exceptions.ConstantValueMissingException;
import com.github.ingaelsta.salarycalculator.exceptions.EmployeeNotFoundException;
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
                .orElseThrow(() -> new EmployeeNotFoundException(String.format("Employee with id %d not found", employeeId)));
        Double socialTaxRate = getConstant(Constants.SOCIAL_TAX_RATE, month, year);

        //social tax
        double taxableAmount = employee.getBaseSalary();
        double calculatedSocialTax = Precision.round(taxableAmount * (socialTaxRate), 2);
        taxableAmount -= calculatedSocialTax;

        double calculatedNonTaxableSum = calculateNonTaxableSum(employee, taxableAmount, month, year);
        double calculatedPayout = taxableAmount;
        taxableAmount = Math.max(taxableAmount - calculatedNonTaxableSum, 0);

        //todo: implement differentiated income tax
        Double incomeTaxRate = getConstant(Constants.INCOME_TAX_RATE, month, year);
        double calculatedIncomeTax = Precision.round(taxableAmount * incomeTaxRate, 2);
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
            Employee employee,
            Double taxableAmount,
            int month,
            int year) {
        Double baseSalary = employee.getBaseSalary();
        Integer dependants = employee.getDependants();
        Boolean useNonTaxableMinimum = employee.getUseNonTaxableMinimum();

        double nonTaxableAmount = 0;
        if ((dependants != null) && (dependants > 0)) {
            Double nonTaxableForDependants = getConstant(Constants.NON_TAXABLE_AMOUNT_FOR_EACH_DEPENDANT, month, year);
            nonTaxableAmount += nonTaxableForDependants * dependants;
    System.out.println();
        }

        if (useNonTaxableMinimum == null || !useNonTaxableMinimum) {
            return Math.min(taxableAmount, nonTaxableAmount);
        }

        Double nonTaxableUpper = getConstant(Constants.NON_TAXABLE_UPPER_BOUND, month, year);
        if (baseSalary > nonTaxableUpper) {
            return Math.min(taxableAmount, nonTaxableAmount);
        }

        Double nonTaxableMinimum = getConstant(Constants.MAX_NON_TAXABLE_MINIMUM, month, year);
        Double nonTaxableLower = getConstant(Constants.NON_TAXABLE_LOWER_BOUND, month, year);
        if (baseSalary <= nonTaxableLower) {
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
            throw new ConstantValueMissingException(String.format("No value found for %s", prettyName));
        }
        Constant constant = constantList.get(0);
        return constant.getVal();
    }
}
