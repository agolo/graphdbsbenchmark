package com.agolo.graphdbsbenchmark.repository;

import com.agolo.graphdbsbenchmark.model.Person;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends ReactiveNeo4jRepository<Person, Long> {
}
