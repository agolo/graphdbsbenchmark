package com.agolo.graphdbsbenchmark.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Record {

    private String personName;
    private String cityName;
    private String countryName;
    private String organizationName;
}
