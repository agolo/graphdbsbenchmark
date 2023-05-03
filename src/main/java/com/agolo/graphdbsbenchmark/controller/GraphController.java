package com.agolo.graphdbsbenchmark.controller;

import com.agolo.graphdbsbenchmark.service.GraphGenerationService;
import com.agolo.graphdbsbenchmark.service.Neo4JPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GraphController implements GraphAPI {

    private GraphGenerationService graphGenerationService;

    private Neo4JPersistenceService neo4JPersistenceService;

    @Autowired
    public GraphController(GraphGenerationService graphGenerationService, Neo4JPersistenceService neo4JPersistenceService) {
        this.graphGenerationService = graphGenerationService;
        this.neo4JPersistenceService = neo4JPersistenceService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createGraph() {
//        return ResponseEntity.ok(graphGenerationService.createGraph());
        neo4JPersistenceService.initializeAndPopulateGraphWithFakeData();
        return ResponseEntity.ok(graphGenerationService.graphRelationships());

    }

    @DeleteMapping
    public ResponseEntity<String> deleteGraph() {
        return ResponseEntity.ok(graphGenerationService.deleteGraph());
    }
}
