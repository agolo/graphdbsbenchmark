package com.agolo.graphdbsbenchmark.service;

import com.github.javafaker.Faker;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.agolo.graphdbsbenchmark.service.MetricsLoggingUtil.*;
import static com.agolo.graphdbsbenchmark.service.Neo4JConstants.*;
import static org.neo4j.driver.Values.parameters;

@Service
public class Neo4JPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(Neo4JPersistenceService.class);

    private final Neo4jClient neo4jClient;

    private static final long NUM_RECORDS = 30000L;
    private static final int SAMPLE_SIZE = 100;

    private final Faker faker;

    @Autowired
    public Neo4JPersistenceService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
        this.faker = new Faker();
    }

    public void initializeAndPopulateGraphWithFakeData() {
        Driver driver = GraphDatabase.driver(NEO4J_URI,
                AuthTokens.basic(NEO4J_USER, NEO4J_PASSWORD));

        long startTime = System.currentTimeMillis();
        List<Long> durations = new ArrayList<>();

        try (Session session = driver.session()) {
            logger.info("Creating {} records", NUM_RECORDS);
            createRecords(startTime, durations, session);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            driver.close();
        }

        logGraphCreationMetrics(logger, NUM_RECORDS, startTime, durations);

    }

    private void createRecords(long startTime, List<Long> durations, Session session) {
        System.out.println("Creating " + NUM_RECORDS + " records");
        for (int i = 0; i < NUM_RECORDS; i++) {

            boolean shouldCreateEntity = faker.bool().bool() && i>40;
            String randomRelationship = faker.options().option(relationshipTypes);

            if (shouldCreateEntity) {
                createNewEntityAndLink(session, randomRelationship);
            } else {
                linkExistingNodes(session, randomRelationship);
            }

            if(i % SAMPLE_SIZE == 0) {
                logSampleMetrics(logger, i, startTime, durations);
            }
        }
    }

    private void createNewEntityAndLink(Session session, String relationshipType) {
        String entityName = fakeEntity(session);
        link(session, relationshipType, entityName);
    }

    private String fakeEntity(Session session) {
        String entityName = "";
        String entityType = faker.options().option(nodeTypes);
        switch (entityType) {
            case PERSON:
                entityName = faker.name().fullName();
                session.run(CREATE_PERSON, parameters(NAME, entityName));
                break;
            case CITY:
                entityName = faker.address().city();
                session.run(CREATE_CITY, parameters(NAME, entityName));
                break;
            case COUNTRY:
                entityName = faker.address().country();
                session.run(CREATE_COUNTRY, parameters(NAME, entityName));
                break;
            case ORGANIZATION:
                entityName = faker.company().name();
                session.run(CREATE_ORGANIZATION, parameters(NAME, entityName));
                break;
        }
        return entityName;
    }

    private void linkExistingNodes(Session session, String randomRelationship) {
        String targetEntitiesQuery = retrieveTargetEntitiesFor(randomRelationship);
        String entityProperty = "name";

        // Retrieve an existing entity from the database
        Result targetEntitiesResult = session.run(targetEntitiesQuery);
        List<Node> targetEntities = targetEntitiesResult.list(record -> record.get(0).asNode());
        Node randomEntity = targetEntities.get(faker.number().numberBetween(0, targetEntities.size()));
        String randomEntityName = randomEntity.get(entityProperty).asString();

        link(session, randomRelationship, randomEntityName);

    }

    private void link(Session session, String randomRelationship, String entityName) {
        // Create the relationship with the existing entity
        switch (randomRelationship) {
            case BORN_IN:
                session.run("MATCH (p:Person {name: $personName}), (c:City {name: $cityName}) CREATE (p)-[:born_in]->(c)",
                        parameters("personName", faker.name().fullName(), "cityName", entityName));
                break;
            case WORKS_FOR:
                session.run("MATCH (p:Person {name: $personName}), (o:Organization {name: $orgName}) CREATE (p)-[:works_for]->(o)",
                        parameters("personName", faker.name().fullName(), "orgName", entityName));
                break;
            case IN:
                session.run("MATCH (c:City {name: $cityName}), (co:Country {name: $countryName}) CREATE (c)-[:in]->(co)",
                        parameters("cityName", faker.address().city(), "countryName", entityName));
                break;
        }
    }

    private String retrieveTargetEntitiesFor(String relationshipType) {
        String entityQuery = "";
        switch (relationshipType) {
            case BORN_IN:
                entityQuery = "MATCH (c:City) RETURN c";
                break;
            case WORKS_FOR:
                entityQuery = "MATCH (o:Organization) RETURN o";
                break;
            case IN:
                entityQuery = "MATCH (co:Country) RETURN co";
                break;
        }
        return entityQuery;
    }

    public String deleteGraph() {
        neo4jClient.query("MATCH (n) DETACH DELETE n").fetch().all();
        return "Graph deleted";
    }
}
