package com.agolo.graphdbsbenchmark.service.neo4j;

public class Neo4JConstants {

    public static final String NEO4J_URI = "bolt://localhost:7687";
    public static final String NEO4J_USER = "neo4j";
    public static final String NEO4J_PASSWORD = "123456789";

    // PARAMETERS
    public static final String NAME = "name";
    public static final String NAME_PARAM_PLACEHOLDER = "${name}";
    public static final String TYPE_PARAM_PLACEHOLDER = "${type}";

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
    public static final String CREATE_NODE_QUERY_TEMPLATE = "CREATE (newNode:${type} {name: ${name}})";

    public static final String MATCH_RANDOM_NODE_QUERY_TEMPLATE = "MATCH (n:${type}) MATCH (n) RETURN n ORDER BY RAND() LIMIT 1";
    public static final String CONNECT_NODES_QUERY_TEMPLATE = "MATCH (s:${sourceType} {name: $sourceName}), (d:${destinationType} {name: $destinationName}) CREATE (s)-[:${relationshipName}]->(d)";
}
