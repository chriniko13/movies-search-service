package com.chriniko.lunatech.movies.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class AlternativeTitle {

    private final String titleId;
    private final Long ordering;
    private final String title;
    private final String region;
    private final String language;
    private final List<String> types;
    private final List<String> attributes;
    private final boolean isOriginalTitle;
}
