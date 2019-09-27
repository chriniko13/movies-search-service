package com.chriniko.lunatech.movies.dto.search.name;

import com.chriniko.lunatech.movies.dto.core.Person;
import com.chriniko.lunatech.movies.dto.core.Title;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PersonGenreInfo {

    private final Person person;

    private final TypecastedInfo typecastedInfo;

    private final int noOfTitles;

    @Singular
    private final List<Title> titles;
}
