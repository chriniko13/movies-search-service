package com.chriniko.lunatech.movies.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class Person {

    private final String id;

    private final String primaryName;
    private final String birthYear;
    private final String deathYear;
    @Singular
    private final List<String> primaryProfessions;
    @Singular
    private final List<String> knownForTitles;
}
