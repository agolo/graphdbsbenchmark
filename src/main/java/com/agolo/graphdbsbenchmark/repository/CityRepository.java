package com.agolo.graphdbsbenchmark.repository;

import com.agolo.graphdbsbenchmark.model.City;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends ReactiveNeo4jRepository<City, Long> {

    // find by name
    Optional<City> findByName(String name);
}
