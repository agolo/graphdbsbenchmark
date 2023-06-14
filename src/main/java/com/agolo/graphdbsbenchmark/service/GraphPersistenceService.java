package com.agolo.graphdbsbenchmark.service;

import java.util.List;

public interface GraphPersistenceService {

    void createEntities(long startTime, List<Long> durations);

    void persistNode(String nodeType, String nodeName);
    
    void createRelationships(long startTime, List<Long> durations);
}
