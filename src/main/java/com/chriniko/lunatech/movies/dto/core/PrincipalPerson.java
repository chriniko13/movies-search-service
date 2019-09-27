package com.chriniko.lunatech.movies.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PrincipalPerson {

    private final String titleId;
    private final Long ordering;
    private final Person personInfo;
    private final String category;
    private final String job;
    private final String characters;
}
