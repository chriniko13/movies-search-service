package com.chriniko.lunatech.movies.dto.search.name;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor

@Builder

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TypecastedInfo {

    private final boolean isTypecasted;
    private final String genreTypecasted;
    private final long halfWork;
    private final long distinctSumOfGenres;
    private final Map<String, Long> genresPlayedGroupByCount;
}
