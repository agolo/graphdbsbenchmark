package com.agolo.graphdbsbenchmark.repository;

import com.agolo.graphdbsbenchmark.model.Country;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface CountryRepository extends ReactiveNeo4jRepository<Country, Long> {

    // find by name
    Optional<Country> findByName(String name);
}
