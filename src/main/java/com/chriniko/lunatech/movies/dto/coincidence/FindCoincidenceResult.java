package com.chriniko.lunatech.movies.dto.coincidence;

import com.chriniko.lunatech.movies.domain.Name;
import com.chriniko.lunatech.movies.dto.core.Title;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FindCoincidenceResult {

    @Singular
    private List<String> executedForNames;

    @Singular
    private List<Name> names;

    private long noOfSharedTitles;

    @Singular
    private List<Title> sharedTitles;
}

