package com.github.ingaelsta.salarycalculator.repository;

import com.github.ingaelsta.salarycalculator.entity.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "employees", path = "employees")
public interface EmployeeRepository extends CrudRepository <Employee, Long> {
}
