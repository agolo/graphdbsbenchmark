package com.agolo.graphdbsbenchmark.service;

public class Neo4JConstants {

    public static final String NEO4J_URI = "bolt://localhost:7687";
    public static final String NEO4J_USER = "neo4j";
    public static final String NEO4J_PASSWORD = "123456789";

    // PARAMETERS
    public static final String NAME = "name";

    // NODES
    public static final String PERSON = "Person";
    public static final String CITY = "City";
    public static final String COUNTRY = "Country";
    public static final String ORGANIZATION = "Organization";
    public static final String[] nodeTypes = {PERSON, CITY, COUNTRY, ORGANIZATION};

    // RELATIONSHIPS
    public static final String BORN_IN = "born_in";
    public static final String WORKS_FOR = "works_for";
    public static final String IN = "in";
    public static final String[] relationshipTypes = {BORN_IN, WORKS_FOR, IN};


    // QUERIES
    public static final String CREATE_PERSON = "CREATE (newNode:Person {name: $name})";
    public static final String CREATE_CITY = "CREATE (newNode:City {name: $name})";
    public static final String CREATE_COUNTRY = "CREATE (newNode:Country {name: $name})";
    public static final String CREATE_ORGANIZATION = "CREATE (newNode:Organization {name: $name})";
}
