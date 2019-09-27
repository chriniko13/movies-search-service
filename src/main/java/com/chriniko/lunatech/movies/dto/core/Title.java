package com.chriniko.lunatech.movies.dto.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)

@EqualsAndHashCode(of = {"id"})

public class Title {

    // BASIC - INFO
    private final String id;
    private final String titleType;
    private final String primaryTitle;
    private final String originalTitle;
    private final boolean isAdult;
    private final String startYear;
    private final String endYear;
    private final Integer runtimeMinutes;
    private final String genres;

    // RATING - INFO
    private final Double averageRating;
    private final Long numVotes;

    // CREW - INFO
    @Singular
    private final List<Person> directors;
    @Singular
    private final List<Person> writers;

    // PRINCIPAL - INFO
    @Singular
    private final List<PrincipalPerson> principals;

    // AKA (Alternative Title) - INFO
    @Singular
    private final List<AlternativeTitle> alternativeTitles;

    // EPISODE - INFO
    @Singular
    private final List<EpisodeInfo> episodeInfos;


}
