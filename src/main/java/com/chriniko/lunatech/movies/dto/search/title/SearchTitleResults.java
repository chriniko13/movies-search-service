package com.chriniko.lunatech.movies.dto.search.title;

import com.chriniko.lunatech.movies.dto.core.Title;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SearchTitleResults {

    private final String query;
    private final long noOfResults;
    private final List<Title> searchResults;
}

