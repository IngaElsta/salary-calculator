package com.github.ingaelsta.salarycalculator.repository;

import com.github.ingaelsta.salarycalculator.entity.Constant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "constants", path = "constants")
public interface ConstantRepository extends CrudRepository <Constant, Long> {
    List<Constant> findByNameOrderByStartDateDesc(@Param("name") String name);
}
