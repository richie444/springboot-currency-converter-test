package com.example.mainservice.repository;

import com.example.mainservice.model.Conversion;
import org.springframework.data.repository.CrudRepository;

public interface ConversionRepository extends CrudRepository<Conversion, Long> {
    // Spring Data JDBC will implement the basic CRUD operations
}