package com.chriniko.lunatech.movies.service.typecasted;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class TypecastedResult {

    private final Map<String, Long> genresPlayedGroupByCount;
    private final long getSumOfGenresPlayed;
}
