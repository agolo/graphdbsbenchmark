package com.agolo.graphdbsbenchmark.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/graph")
public interface GraphAPI {

    @PostMapping
    ResponseEntity<Void> createGraph();

    @DeleteMapping
    ResponseEntity<String> deleteGraph();
}
