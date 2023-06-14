package com.agolo.graphdbsbenchmark.controller;

import com.agolo.graphdbsbenchmark.service.neo4j.Neo4JPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GraphController implements GraphAPI {

    private Neo4JPersistenceService neo4JPersistenceService;

    @Autowired
    public GraphController(Neo4JPersistenceService neo4JPersistenceService) {
        this.neo4JPersistenceService = neo4JPersistenceService;
    }

    @Override
    public ResponseEntity<Void> createGraph() {
        neo4JPersistenceService.initializeAndPopulateGraphWithFakeData();
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<String> deleteGraph() {
        neo4JPersistenceService.deleteGraph();
        return ResponseEntity.ok().build();
    }
}
