package com.agolo.graphdbsbenchmark.service.arangodb;

public class ArangoDBConstants {

    public static final String DB_NAME = "graphdbbenchmark";
    public static final String GRAPH_NAME = "graph";
    public static final String PERSON_COLLECTION = "person";
    public static final String CITY_COLLECTION = "city";
    public static final String COUNTRY_COLLECTION = "country";
    public static final String ORGANIZATION_COLLECTION = "organization";
    public static final String BORN_IN_EDGE_COLLECTION = "born_in";
    public static final String WORKS_FOR_EDGE_COLLECTION = "works_for";
    public static final String IN_EDGE_COLLECTION = "in";

    public static final String PERSON = "person";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String ORGANIZATION = "organization";
    public static final String BORN_IN = "born_in";
    public static final String WORKS_FOR = "works_for";
    public static final String IN = "in";

    public static final String[] NODE_TYPES = {PERSON, CITY, COUNTRY, ORGANIZATION};
    public static final String[] RELATIONSHIP_TYPES = {BORN_IN, WORKS_FOR, IN};
}
