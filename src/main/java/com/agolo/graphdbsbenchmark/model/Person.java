package com.agolo.graphdbsbenchmark.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;


@Node
@Data
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("email")
    private String email;

    @Relationship(type = "bornIn")
    private City city;

    @Relationship(type = "worksFor")
    private Organization organization;

}
