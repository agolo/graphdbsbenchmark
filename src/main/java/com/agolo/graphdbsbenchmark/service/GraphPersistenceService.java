package com.agolo.graphdbsbenchmark.service;

import com.agolo.graphdbsbenchmark.model.Record;
import com.github.javafaker.Faker;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

@Service
public interface GraphPersistenceService {


    Map<String, Object> createGraph();

    void createRecord(Session session);

    void createNodes(Session session, Record record);

    void createRelationships(Session session, Record record);

    Map<String, Object> graphRelationships();

    String deleteGraph();
}
