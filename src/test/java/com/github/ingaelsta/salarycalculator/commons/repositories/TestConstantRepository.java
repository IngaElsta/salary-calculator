package com.github.ingaelsta.salarycalculator.commons.repositories;

import com.github.ingaelsta.salarycalculator.entity.Constant;
import com.github.ingaelsta.salarycalculator.repository.ConstantRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TestConstantRepository implements ConstantRepository {
    private final Map<Long, Constant> constantMap;
    
    public TestConstantRepository() {
        constantMap = new HashMap<>();
        constantMap.put(1L, new Constant(1L, "MAX_NON_TAXABLE_MINIMUM", 350.0, LocalDate.parse("2022-01-01"), LocalDate.parse("2022-06-30")));
        constantMap.put(2L, new Constant(2L, "MAX_NON_TAXABLE_MINIMUM", 500.0, LocalDate.parse("2022-07-01"), null));
        constantMap.put(3L, new Constant(3L, "NON_TAXABLE_LOWER_BOUND", 500.0, LocalDate.parse("2022-07-01"), null));
        constantMap.put(4L, new Constant(4L, "NON_TAXABLE_UPPER_BOUND", 1800.0, LocalDate.parse("2022-07-01"), null));
        constantMap.put(5L, new Constant(5L, "NON_TAXABLE_AMOUNT_FOR_EACH_DEPENDANT", 250.0, LocalDate.parse("2022-07-01"), null));
        constantMap.put(6L, new Constant(6L, "INCOME_TAX_RATE", 0.2, LocalDate.parse("2022-07-01"), null));
        constantMap.put(7L, new Constant(7L, "SOCIAL_TAX_RATE", 0.105, LocalDate.parse("2022-07-01"), null));

    }    

    @Override
    public <S extends Constant> S save(S constant) {
        constantMap.put(constant.getId(), constant);
        return constant;
    }

    @Override
    public <S extends Constant> Iterable<S> saveAll( Iterable<S> constants) {
        for (S constant : constants) {
            save(constant);
        }
        return constants;
    }

    @Override
    public Optional<Constant> findById(Long id) {
        return Optional.of(constantMap.get(id));
    }

    @Override
    public boolean existsById(Long id) {
        return constantMap.containsKey(id);
    }

    @Override
    public Iterable<Constant> findAll() {
        return constantMap.values();
    }

    @Override
    public Iterable<Constant> findAllById(Iterable<Long> ids) {
        return StreamSupport.stream(ids.spliterator(), false)
                .filter(constantMap::containsKey)
                .map(constantMap::get)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return constantMap.size();
    }

    @Override
    public void deleteById(Long id) {
        constantMap.remove(id);
    }

    @Override
    public void delete(Constant constant) {
        constantMap.remove(constant.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        for (Long id: ids) {
            deleteById(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends Constant> constants) {
        for (Constant e : constants) {
            deleteById(e.getId());
        }
    }

    @Override
    public void deleteAll() {
        constantMap.clear();
    }

    @Override
    public List<Constant> findByNameOrderByStartDateDesc(String name) {
        return constantMap.values().stream()
                .filter(constant -> Objects.equals(constant.getName(), name))
                .sorted(Comparator.reverseOrder())
                .toList();
    }
}
