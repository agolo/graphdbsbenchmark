package com.agolo.graphdbsbenchmark.repository;

import com.agolo.graphdbsbenchmark.model.Organization;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends ReactiveNeo4jRepository<Organization, Long> {

    // find by name
    Optional<Organization> findByName(String name);
}
