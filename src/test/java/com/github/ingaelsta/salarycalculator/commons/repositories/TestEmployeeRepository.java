package com.github.ingaelsta.salarycalculator.commons.repositories;

import com.github.ingaelsta.salarycalculator.entity.Employee;
import com.github.ingaelsta.salarycalculator.repository.EmployeeRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TestEmployeeRepository implements EmployeeRepository {
    private final Map<Long, Employee> employeeMap;
    
    public TestEmployeeRepository() {
        employeeMap = new HashMap<>();
        //below lower bound for non-taxable minimum differentiation, no dependants, uses non-taxable minimum
        employeeMap.put(1L, new Employee(1L, "Low", "Salary", BigDecimal.valueOf(400.00), 0, true));
        //below lower bound for non-taxable minimum differentiation, no dependants, does not use non-taxable minimum
        employeeMap.put(2L, new Employee(2L, "Low", "Salary", BigDecimal.valueOf(400.00), 0, false));
        //below lower bound for non-taxable minimum differentiation, 1 dependant, does not use non-taxable minimum
        employeeMap.put(3L, new Employee(3L, "Low", "Salary", BigDecimal.valueOf(400.00), 1, false));
        //below lower bound for non-taxable minimum differentiation, 2 dependants, does not use non-taxable minimum
        employeeMap.put(4L, new Employee(4L, "Low", "Salary", BigDecimal.valueOf(400.00), 2, false));
        //non-taxable minimum differentiation, no dependants, uses non-taxable minimum
        employeeMap.put(5L, new Employee(5L, "Mid", "Salary", BigDecimal.valueOf(1400.00), 0, true));
        //non-taxable minimum differentiation, no dependants, does not use non-taxable minimum
        employeeMap.put(6L, new Employee(6L, "Mid", "Salary", BigDecimal.valueOf(1400.00), 0, false));
        //non-taxable minimum differentiation, 2 dependants, uses non-taxable minimum
        employeeMap.put(7L, new Employee(7L, "Mid", "Salary", BigDecimal.valueOf(1400.00), 2, true));
        //non-taxable minimum differentiation, 2 dependants, does not use non-taxable minimum
        employeeMap.put(8L, new Employee(8L, "Mid", "Salary", BigDecimal.valueOf(1400.00), 2, false));

    }    

    @Override
    public <S extends Employee> S save(S entity) {
        employeeMap.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends Employee> Iterable<S> saveAll( Iterable<S> entities) {
        for (S entity : entities) {
            save(entity);
        }
        return entities;
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return Optional.of(employeeMap.get(id));
    }

    @Override
    public boolean existsById(Long id) {
        return employeeMap.containsKey(id);
    }

    @Override
    public Iterable<Employee> findAll() {
        return employeeMap.values();
    }

    @Override
    public Iterable<Employee> findAllById(Iterable<Long> ids) {
        return StreamSupport.stream(ids.spliterator(), false)
                .filter(employeeMap::containsKey)
                .map(employeeMap::get)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return employeeMap.size();
    }

    @Override
    public void deleteById(Long id) {
        employeeMap.remove(id);
    }

    @Override
    public void delete(Employee entity) {
        employeeMap.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        for (Long id: ids) {
            deleteById(id);
        }
    }

    @Override
    public void deleteAll( Iterable<? extends Employee> entities) {
        for (Employee e : entities) {
            deleteById(e.getId());
        }
    }

    @Override
    public void deleteAll() {
        employeeMap.clear();
    }
}
