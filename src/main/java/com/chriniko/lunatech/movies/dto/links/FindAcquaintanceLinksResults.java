package com.chriniko.lunatech.movies.dto.links;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FindAcquaintanceLinksResults {

    private final String providedSourceFullName;
    private final String providedTargetName;
    private final List<FindAcquaintanceLinksResult> results;
}
