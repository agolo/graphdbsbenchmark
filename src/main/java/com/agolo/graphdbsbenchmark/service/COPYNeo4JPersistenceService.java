package com.agolo.graphdbsbenchmark.service;

import com.agolo.graphdbsbenchmark.model.Record;
import com.github.javafaker.Faker;
import org.neo4j.driver.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

@Service
public class COPYNeo4JPersistenceService implements GraphPersistenceService {

    static final long NUM_RECORDS = 3L;
    static final String NEO4J_URI = "bolt://localhost:7687";
    static final String NEO4J_USER = "neo4j";
    static final String NEO4J_PASSWORD = "123456789";
    final Neo4jClient neo4jClient;

    final Faker faker;

    @Autowired
    public COPYNeo4JPersistenceService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
        this.faker = new Faker();
    }

    @Override
    public Map<String, Object> createGraph() {
        // Connect to the Neo4J database
        Driver driver = GraphDatabase.driver(NEO4J_URI,
                AuthTokens.basic(NEO4J_USER, NEO4J_PASSWORD));

        try(Session session = driver.session()) {
            for (int i = 0; i < NUM_RECORDS; i++) {
                createRecord(session);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }

        return graphRelationships();
    }

    @Override
    public void createRecord(Session session) {

        Record fakeRecord = fakeRecord();

        createNodes(session, fakeRecord);

        createRelationships(session, fakeRecord);
    }

    public Record fakeRecord() {
        // Generate data for the nodes
        String personName = faker.name().fullName();
        String cityName = faker.address().city();
        String countryName = faker.address().country();
        String organizationName = faker.company().name();

        return new Record(personName, cityName, countryName, organizationName);
    }
    @Override
    public void createNodes(Session session, Record record) {

        String personName = record.getPersonName();
        String cityName = record.getCityName();
        String countryName = record.getCountryName();
        String organizationName = record.getOrganizationName();

        // Create the nodes
        session.run("CREATE (p:Person {name: $name})", parameters("name", personName));
        session.run("CREATE (c:City {name: $name})", parameters("name", cityName));
        session.run("CREATE (co:Country {name: $name})", parameters("name", countryName));
        session.run("CREATE (o:Organization {name: $name})", parameters("name", organizationName));
    }

    @Override
    public void createRelationships(Session session, Record record) {

        String personName = record.getPersonName();
        String cityName = record.getCityName();
        String countryName = record.getCountryName();
        String organizationName = record.getOrganizationName();

        // Create the relationships
        session.run("MATCH (p:Person {name: $personName}), (c:City {name: $cityName}) CREATE (p)-[:born_in]->(c)",
                parameters("personName", personName, "cityName", cityName));
        session.run("MATCH (p:Person {name: $personName}), (o:Organization {name: $orgName}) CREATE (p)-[:works_for]->(o)",
                parameters("personName", personName, "orgName", organizationName));
        session.run("MATCH (c:City {name: $cityName}), (co:Country {name: $countryName}) CREATE (c)-[:in]->(co)",
                parameters("cityName", cityName, "countryName", countryName));
        session.run("MATCH (o:Organization {name: $orgName}), (co:Country {name: $countryName}) CREATE (o)-[:in]->(co)",
                parameters("orgName", organizationName, "countryName", countryName));
    }



    @Override
    public Map<String, Object> graphRelationships() {
        List<Map<String, Object>> personBornIn = neo4jClient.query("MATCH (p:Person)-[r:born_in]->(c:City) RETURN p.name AS person, c.name AS city").fetch().all().stream().toList();
        List<Map<String, Object>> personWorksFor = neo4jClient.query("MATCH (p:Person)-[r:works_for]->(o:Organization) RETURN p.name AS person, o.name AS organization").fetch().all().stream().toList();
        List<Map<String, Object>> cityIn = neo4jClient.query("MATCH (c:City)-[r:in]->(co:Country) RETURN c.name AS city, co.name AS country").fetch().all().stream().toList();
        List<Map<String, Object>> organizationIn = neo4jClient.query("MATCH (o:Organization)-[r:in]->(co:Country) RETURN o.name AS organization, co.name AS country").fetch().all().stream().toList();

        Map<String, Object> graph = Map.of("personBornIn", personBornIn,
                "personWorksFor", personWorksFor,
                "cityIn", cityIn,
                "organizationIn", organizationIn);

        return graph;
    }

    private void graphEntities() {

        // Retrieve graph data
        List<Object> persons = new ArrayList<>(neo4jClient.query("MATCH (p:Person) RETURN p").fetchAs(Object.class).all());
        List<Object> organizations = new ArrayList<>(neo4jClient.query("MATCH (o:Organization) RETURN o").fetchAs(Object.class).all());
        List<Object> countries = new ArrayList<>(neo4jClient.query("MATCH (c:Country) RETURN c").fetchAs(Object.class).all());
        List<Object> cities = new ArrayList<>(neo4jClient.query("MATCH (c:City) RETURN c").fetchAs(Object.class).all());

    }

    @Override
    public String deleteGraph() {
        neo4jClient.query("MATCH (n) DETACH DELETE n").fetch().all();
        return "Graph deleted";
    }
}
