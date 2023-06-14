package com.agolo.graphdbsbenchmark.service.neo4j;

import com.agolo.graphdbsbenchmark.service.GraphPersistenceService;
import com.github.javafaker.Faker;
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
import static com.agolo.graphdbsbenchmark.service.neo4j.Neo4JConstants.*;

@Service
public class Neo4JPersistenceService implements GraphPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(Neo4JPersistenceService.class);

    private final Neo4jClient neo4jClient;

    private static final long NUM_ENTITIES = 30L;
    private static final int SAMPLE_SIZE = 100;

    private final Faker faker;

    @Autowired
    public Neo4JPersistenceService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
        this.faker = new Faker();
    }

    public void initializeAndPopulateGraphWithFakeData() {

        long startTime = System.currentTimeMillis();
        List<Long> durations = new ArrayList<>();

        logger.info("Creating {} records", NUM_ENTITIES);
        createEntities(startTime, durations);
        createRelationships(startTime, durations);

        logGraphCreationMetrics(logger, NUM_ENTITIES, startTime, durations);

    }

    public void createEntities(long startTime, List<Long> durations) {
        System.out.println("Creating " + NUM_ENTITIES + " entities");
        for (int i = 0; i < NUM_ENTITIES; i++) {
            createFakeNode();

            if (i % SAMPLE_SIZE == 0) {
                logSampleMetrics(logger, i, startTime, durations);
            }
        }
    }

    private void createFakeNode() {
        String entityType = faker.options().option(nodeTypes);
        switch (entityType) {
            case PERSON:
                persistNode(PERSON, faker.name().fullName());
                break;
            case CITY:
                persistNode(CITY, faker.address().city());
                break;
            case COUNTRY:
                persistNode(COUNTRY, faker.address().country());
                break;
            case ORGANIZATION:
                persistNode(ORGANIZATION, faker.company().name());
                break;
        }
    }

    public void persistNode(String nodeType, String nodeName) {
        String createNodeQuery = CREATE_NODE_QUERY_TEMPLATE
                .replace(TYPE_PARAM_PLACEHOLDER, nodeType)
                .replace(NAME_PARAM_PLACEHOLDER, "\"" + nodeName + "\"");
        System.out.println(String.format("Creating node of type %s with name %s", nodeType, nodeName));
        neo4jClient.query(createNodeQuery).fetch().all();
    }

    public void createRelationships(long startTime, List<Long> durations) {
        System.out.println("Creating relationships");
        for (int i = 0; i < NUM_ENTITIES; i++) {
            String randomRelationship = faker.options().option(relationshipTypes);
            connectTwoRandomNodes(randomRelationship);

            if (i % SAMPLE_SIZE == 0) {
                logSampleMetrics(logger, i, startTime, durations);
            }
        }
    }

    private void connectTwoRandomNodes(String relationshipType) {
        switch (relationshipType) {
            case BORN_IN:
                connect(PERSON, BORN_IN, CITY);
                break;
            case WORKS_FOR:
                connect(PERSON, WORKS_FOR, ORGANIZATION);
                break;
            case IN:
                boolean coinFlip = faker.bool().bool();
                if (coinFlip) {
                    connect(CITY, IN, COUNTRY);
                } else {
                    connect(ORGANIZATION, IN, COUNTRY);
                }
                break;
        }
    }

    private void connect(String sourceType, String relationshipName, String destinationType) {
        String sourceQuery = MATCH_RANDOM_NODE_QUERY_TEMPLATE.replace(TYPE_PARAM_PLACEHOLDER, sourceType);
        String destinationQuery = MATCH_RANDOM_NODE_QUERY_TEMPLATE.replace(TYPE_PARAM_PLACEHOLDER, destinationType);
        Node sourceNode = neo4jClient.query(sourceQuery).fetch().one()
                .map(record -> (Node)record.get("n"))
                .orElseThrow(() -> new RuntimeException("No source node found"));

        Node destinationNode = neo4jClient.query(destinationQuery).fetch().one()
                .map(record -> (Node)record.get("n"))
                .orElseThrow(() -> new RuntimeException("No destination node found"));

        String connectNodesQuery = CONNECT_NODES_QUERY_TEMPLATE
                .replace("${sourceType}", sourceType)
                .replace("${destinationType}", destinationType)
                .replace("$sourceName", "\"" + sourceNode.get("name").asString() + "\"")
                .replace("$destinationName", "\"" + destinationNode.get("name").asString() + "\"")
                .replace("${relationshipName}", relationshipName.toUpperCase(Locale.ROOT));
        neo4jClient.query(connectNodesQuery).fetch().all();
        System.out.println(String.format("Connecting {{%s}} %s to {{%s}} %s", sourceType, sourceNode.get("name").asString(), destinationType, destinationNode.get("name").asString()));
    }


    public void deleteGraph() {
        neo4jClient.query("MATCH (n) DETACH DELETE n").fetch().all();
    }
}