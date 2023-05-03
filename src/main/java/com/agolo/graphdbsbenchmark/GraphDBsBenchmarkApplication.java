package com.agolo.graphdbsbenchmark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GraphDBsBenchmarkApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphDBsBenchmarkApplication.class, args);
	}


	/*
	 Next Steps:
	 - Use the new code to create new entities or link to existing entities
	 - Implement the ArrangoDB and JanusGraph (??) services/clients
	 - Read about benchmarking and performing load tests
	*/
}
