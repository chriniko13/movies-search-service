package com.chriniko.lunatech.movies.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class EpisodeInfo {

    private final String id;
    private final String titleId;
    private final Long seasonNumber;
    private final Long episodeNumber;

}
