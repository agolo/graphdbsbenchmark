package com.agolo.graphdbsbenchmark.service;

import com.github.javafaker.Faker;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.agolo.graphdbsbenchmark.service.MetricsLoggingUtil.*;
import static com.agolo.graphdbsbenchmark.service.Neo4JConstants.*;

@Service
public class Neo4JPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(Neo4JPersistenceService.class);

    private final Neo4jClient neo4jClient;

    private static final long NUM_ENTITIES = 30000L;
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
            logger.info("Creating {} records", NUM_ENTITIES);
            createEntities(startTime, durations, session);
            createRelationships(startTime, durations, session);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            driver.close();
        }

        logGraphCreationMetrics(logger, NUM_ENTITIES, startTime, durations);

    }

    private void createEntities(long startTime, List<Long> durations, Session session) {
        System.out.println("Creating " + NUM_ENTITIES + " entities");
        for (int i = 0; i < NUM_ENTITIES; i++) {
            createFakeNode(session);

            if(i % SAMPLE_SIZE == 0) {
                logSampleMetrics(logger, i, startTime, durations);
            }
        }
    }

    private void createFakeNode(Session session) {
        String entityType = faker.options().option(nodeTypes);
        switch (entityType) {
            case PERSON:
                persistNode(PERSON, faker.name().fullName(), session);
                break;
            case CITY:
                persistNode(CITY, faker.address().city(), session);
                break;
            case COUNTRY:
                persistNode(COUNTRY, faker.address().country(), session);
                break;
            case ORGANIZATION:
                persistNode(ORGANIZATION, faker.company().name(), session);
                break;
        }
    }

    private void persistNode(String nodeType, String nodeName, Session session) {
        String createNodeQuery = CREATE_NODE_QUERY_TEMPLATE
                .replace(TYPE_PARAM_PLACEHOLDER, nodeType)
                .replace(NAME_PARAM_PLACEHOLDER, "\""+ nodeName + "\"");
        session.run(createNodeQuery);
    }

    private void createRelationships(long startTime, List<Long> durations, Session session) {
        System.out.println("Creating relationships");
        for (int i = 0; i < NUM_ENTITIES; i++) {
            String randomRelationship = faker.options().option(relationshipTypes);
            connectTwoRandomNodes(session, randomRelationship);

            if(i % SAMPLE_SIZE == 0) {
                logSampleMetrics(logger, i, startTime, durations);
            }
        }
    }

    private void connectTwoRandomNodes(Session session, String relationshipType) {

        switch (relationshipType) {
            case BORN_IN:
                connect(PERSON, BORN_IN, CITY, session);
                break;
            case WORKS_FOR:
                connect(PERSON, WORKS_FOR, ORGANIZATION, session);
                break;
            case IN:
                boolean coinFlip = faker.bool().bool();
                if (coinFlip) {
                    connect(CITY, IN, COUNTRY, session);
                } else {
                    connect(ORGANIZATION, IN, COUNTRY, session);
                }
                break;
        }
    }

    private void connect(String sourceType, String relationshipName, String destinationType, Session session) {
        String sourceQuery = MATCH_RANDOM_NODE_QUERY_TEMPLATE.replace(TYPE_PARAM_PLACEHOLDER, sourceType);
        String destinationQuery = MATCH_RANDOM_NODE_QUERY_TEMPLATE.replace(TYPE_PARAM_PLACEHOLDER, destinationType);
        Record sourceRecord = session.run(sourceQuery).next();
        Record destinationRecord = session.run(destinationQuery).next();
        Node sourceNode = sourceRecord.get("n").asNode();
        Node destinationNode = destinationRecord.get("n").asNode();
        String connectNodesQuery = CONNECT_NODES_QUERY_TEMPLATE
                .replace("${sourceType}", sourceType)
                .replace("${destinationType}", destinationType)
                .replace("$sourceName", "\"" + sourceNode.get("name").asString() + "\"" )
                .replace("$destinationName", "\"" + destinationNode.get("name").asString() + "\"")
                .replace("${relationshipName}", relationshipName.toUpperCase(Locale.ROOT));
        session.run(connectNodesQuery);
    }

    public String deleteGraph() {
        neo4jClient.query("MATCH (n) DETACH DELETE n").fetch().all();
        return "Graph deleted";
    }
}
