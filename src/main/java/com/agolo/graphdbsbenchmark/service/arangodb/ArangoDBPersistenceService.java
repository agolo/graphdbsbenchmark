package com.agolo.graphdbsbenchmark.service.arangodb;

import com.agolo.graphdbsbenchmark.exception.GraphExistsException;
import com.agolo.graphdbsbenchmark.service.GraphPersistenceService;
import com.arangodb.*;
import com.arangodb.entity.*;
import com.arangodb.model.GraphCreateOptions;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.agolo.graphdbsbenchmark.service.MetricsLoggingUtil.*;
import static com.agolo.graphdbsbenchmark.service.arangodb.ArangoDBConstants.*;

@Service
@Slf4j
public class ArangoDBPersistenceService implements GraphPersistenceService {


    private final ArangoDB arangoDB;
    private final ArangoDatabase arangoDatabase;

    private static final long NUM_ENTITIES = 30L;
    private static final int SAMPLE_SIZE = 100;

    private final Faker faker;

    @Autowired
    public ArangoDBPersistenceService(ArangoDB arangoDB) {
        this.arangoDB = arangoDB;
        this.arangoDatabase = arangoDB.db(DB_NAME);
        this.faker = new Faker();
    }

    public void initializeAndPopulateGraphWithFakeData() {

        long startTime = System.currentTimeMillis();
        List<Long> durations = new ArrayList<>();

        createGraphIfNotExists();

        log.info("Creating {} records", NUM_ENTITIES);
        createEntities(startTime, durations);
        createRelationships(startTime, durations);

        logGraphCreationMetrics(log, NUM_ENTITIES, startTime, durations);

    }

    private void createGraphIfNotExists() {
        try {
            if (arangoDatabase.graph(GRAPH_NAME) == null) {
                List<EdgeDefinition> edgeDefinitions = initializeEdgeDefinitionsOfGraph();
                arangoDatabase.createGraph(GRAPH_NAME, edgeDefinitions);
            }
        } catch (ArangoDBException e) {
            log.error("Failed to create graph", e);
            throw new GraphExistsException("Failed to create graph " + GRAPH_NAME);
        }
    }

    private static List<EdgeDefinition> initializeEdgeDefinitionsOfGraph() {
        GraphCreateOptions options = new GraphCreateOptions();
        options.orphanCollections(String.valueOf(Arrays.asList(PERSON_COLLECTION, CITY_COLLECTION, COUNTRY_COLLECTION, ORGANIZATION_COLLECTION)));
        options.getEdgeDefinitions().add(new EdgeDefinition().collection(BORN_IN_EDGE_COLLECTION).from(PERSON_COLLECTION).to(CITY_COLLECTION));
        options.getEdgeDefinitions().add(new EdgeDefinition().collection(WORKS_FOR_EDGE_COLLECTION).from(PERSON_COLLECTION).to(ORGANIZATION_COLLECTION));
        options.getEdgeDefinitions().add(new EdgeDefinition().collection(IN_EDGE_COLLECTION).from(CITY_COLLECTION, ORGANIZATION_COLLECTION).to(COUNTRY_COLLECTION));
        return options.getEdgeDefinitions().stream().toList();
    }

    @Override
    public void createEntities(long startTime, List<Long> durations) {
        log.info("Creating " + NUM_ENTITIES + " entities");
        for (int i = 0; i < NUM_ENTITIES; i++) {
            createFakeNode();

            if (i % SAMPLE_SIZE == 0) {
                logSampleMetrics(log, i, startTime, durations);
            }
        }
    }

    private void createFakeNode() {
        String entityType = faker.options().option(NODE_TYPES);
        switch (entityType) {
            case PERSON:
                persistNode(PERSON_COLLECTION, faker.name().fullName());
                break;
            case CITY:
                persistNode(CITY_COLLECTION, faker.address().city());
                break;
            case COUNTRY:
                persistNode(COUNTRY_COLLECTION, faker.address().country());
                break;
            case ORGANIZATION:
                persistNode(ORGANIZATION_COLLECTION, faker.company().name());
                break;
        }
    }

    @Override
    public void persistNode(String nodeType, String nodeName) {
        ArangoCollection collection = arangoDatabase.collection(nodeType);
        BaseDocument document = new BaseDocument();
        document.addAttribute("name", nodeName);
        collection.insertDocument(document);
        log.info(String.format("Creating node of type %s with name %s", nodeType, nodeName));
    }

    @Override
    public void createRelationships(long startTime, List<Long> durations) {
        log.info("Creating relationships");
        for (int i = 0; i < NUM_ENTITIES; i++) {
            String randomRelationship = faker.options().option(RELATIONSHIP_TYPES);
            connectTwoRandomNodes(randomRelationship);

            if (i % SAMPLE_SIZE == 0) {
                logSampleMetrics(log, i, startTime, durations);
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
        String sourceCollectionName = getTypeCollectionName(sourceType);
        String destinationCollectionName = getTypeCollectionName(destinationType);

        String sourceQuery = String.format(
                "FOR n IN %s LIMIT 1 SORT RAND() RETURN n", sourceCollectionName
        );
        String destinationQuery = String.format(
                "FOR n IN %s LIMIT 1 SORT RAND() RETURN n", destinationCollectionName
        );

        ArangoCursor<BaseDocument> sourceCursor = arangoDatabase.query(sourceQuery, BaseDocument.class);
        ArangoCursor<BaseDocument> destinationCursor = arangoDatabase.query(destinationQuery, BaseDocument.class);

        BaseDocument sourceNode = sourceCursor.iterator().next();
        BaseDocument destinationNode = destinationCursor.iterator().next();

        String sourceKey = sourceNode.getKey();
        String destinationKey = destinationNode.getKey();

        String insertEdgeQuery = String.format(
                "INSERT { _from: '%s/%s', _to: '%s/%s', type: '%s' } IN %s",
                sourceCollectionName, sourceKey, destinationCollectionName, destinationKey, relationshipName, relationshipName
        );
        arangoDatabase.query(insertEdgeQuery, Void.class);

        String message = String.format(
                "Connecting {%s} %s to {%s} %s",
                sourceType,
                sourceNode.getProperties().get("name"),
                destinationType,
                destinationNode.getProperties().get("name")
        );

        log.info(message);
    }

    private String getTypeCollectionName(String sourceType) {
        switch (sourceType) {
            case PERSON:
                return PERSON_COLLECTION;
            case CITY:
                return CITY_COLLECTION;
            case COUNTRY:
                return COUNTRY_COLLECTION;
            case ORGANIZATION:
                return ORGANIZATION_COLLECTION;
            default:
                throw new RuntimeException("Invalid type");
        }
    }


}