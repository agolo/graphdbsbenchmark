package com.agolo.graphdbsbenchmark.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node
@Data
public class Country {

    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

}