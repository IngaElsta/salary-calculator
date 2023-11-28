package com.github.ingaelsta.salarycalculator.service;

import com.github.ingaelsta.salarycalculator.entity.Constant;
import com.github.ingaelsta.salarycalculator.entity.Employee;
import com.github.ingaelsta.salarycalculator.entity.Salary;
import com.github.ingaelsta.salarycalculator.exceptions.ConstantValueMissingException;
import com.github.ingaelsta.salarycalculator.exceptions.EmployeeNotFoundException;
import com.github.ingaelsta.salarycalculator.repository.ConstantRepository;
import com.github.ingaelsta.salarycalculator.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class SalaryCalculatorService {

    private final ConstantRepository constantRepository;
    private final EmployeeRepository employeeRepository;
    private final RoundingMode halfUp = RoundingMode.HALF_UP;

    public SalaryCalculatorService(ConstantRepository constantRepository, EmployeeRepository employeeRepository) {
        this.constantRepository = constantRepository;
        this.employeeRepository = employeeRepository;
    }

    public Salary getSalary(Long employeeId, Integer month, Integer year) {
        //todo: implement error handling
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(String.format("Employee with id %d not found", employeeId)));
        BigDecimal socialTaxRate = getConstant(Constant.SOCIAL_TAX_RATE, month, year);

        //social tax
        BigDecimal taxableAmount = employee.getBaseSalary();
        BigDecimal calculatedSocialTax = taxableAmount.multiply(socialTaxRate).setScale(2, halfUp);
        taxableAmount = taxableAmount.subtract(calculatedSocialTax);

        BigDecimal calculatedNonTaxableSum = calculateNonTaxableSum(employee, taxableAmount, month, year);
        BigDecimal taxableAmountForIncomeTax = (taxableAmount.subtract(calculatedNonTaxableSum)).max(BigDecimal.ZERO);

        //todo: implement differentiated income tax
        BigDecimal incomeTaxRate = getConstant(Constant.INCOME_TAX_RATE, month, year);
        BigDecimal calculatedIncomeTax = taxableAmountForIncomeTax.multiply(incomeTaxRate).setScale(2, halfUp);
        BigDecimal calculatedPayout = taxableAmount.subtract(calculatedIncomeTax);

        return new Salary(employeeId,
                month,
                year,
                employee.getBaseSalary().setScale(2, halfUp),
                calculatedNonTaxableSum.setScale(2, halfUp),
                calculatedIncomeTax.setScale(2, halfUp),
                calculatedSocialTax.setScale(2, halfUp),
                calculatedPayout.setScale(2, halfUp));
    }

    protected BigDecimal calculateNonTaxableSum (
            Employee employee,
            BigDecimal taxableAmount,
            int month,
            int year) {
        BigDecimal baseSalary = employee.getBaseSalary();
        Integer dependants = employee.getDependants();
        Boolean useNonTaxableMinimum = employee.getUseNonTaxableMinimum();

        BigDecimal nonTaxableAmount = BigDecimal.ZERO;
        if (dependants > 0) {
            BigDecimal nonTaxableForDependants = getConstant(Constant.NON_TAXABLE_AMOUNT_FOR_EACH_DEPENDANT, month, year);
            BigDecimal CalculatedNonTaxableForDependants = nonTaxableForDependants.multiply(BigDecimal.valueOf(dependants));
            nonTaxableAmount = nonTaxableAmount
                    .add(CalculatedNonTaxableForDependants) ;
        }

        if (!useNonTaxableMinimum) {
            return taxableAmount.min(nonTaxableAmount);
        }

        BigDecimal nonTaxableUpper = getConstant(Constant.NON_TAXABLE_UPPER_BOUND, month, year);
        if (baseSalary.compareTo(nonTaxableUpper) > 0) { //>
            return taxableAmount.min(nonTaxableAmount);
        }

        BigDecimal nonTaxableMinimum = getConstant(Constant.MAX_NON_TAXABLE_MINIMUM, month, year);
        BigDecimal nonTaxableLower = getConstant(Constant.NON_TAXABLE_LOWER_BOUND, month, year);
        if (baseSalary.compareTo(nonTaxableLower) <= 0) { //<=
            nonTaxableAmount = nonTaxableAmount.add(taxableAmount.min(nonTaxableMinimum));
            return taxableAmount.min(nonTaxableAmount);
        }
        BigDecimal coefficient = nonTaxableMinimum
                .divide(nonTaxableUpper.subtract(nonTaxableLower), 6, halfUp);
//        K = GNMmax / (AImax – AImin);
        BigDecimal diffNonTaxableAmount = nonTaxableMinimum
                .subtract(coefficient.multiply(baseSalary.subtract(nonTaxableLower)).setScale(2, halfUp));
        nonTaxableAmount = diffNonTaxableAmount.add(nonTaxableAmount)
                .min( taxableAmount);
//        GDNM = GNMmax – K x (AI – AImin)
        return (taxableAmount.min(nonTaxableAmount)).setScale(2, halfUp);
    }

    private BigDecimal getConstant(String constantName, Integer month, Integer year) {
        //todo: implement check for correct period
        List<Constant> constantList = constantRepository.findByNameOrderByStartDateDesc(constantName);
        if (constantList.isEmpty()) {
            String prettyName = constantName.toLowerCase().replace("_", " ");
            throw new ConstantValueMissingException(String.format("No value found for %s", prettyName));
        }
        Constant constant = constantList.get(0);
        return BigDecimal.valueOf(constant.getVal());
    }
}
