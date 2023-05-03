package com.agolo.graphdbsbenchmark.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
@Data
public class City {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @Relationship(type = "inCountry")
    private Country country;

}


