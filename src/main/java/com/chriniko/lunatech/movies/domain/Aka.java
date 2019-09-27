package com.chriniko.lunatech.movies.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
    Contains the following information for titles:

    titleId (string) - a tconst, an alphanumeric unique identifier of the title
    ordering (integer) – a number to uniquely identify rows for a given titleId

    title (string) – the localized title
    region (string) - the region for this version of the title
    language (string) - the language of the title
    types (array) - Enumerated set of attributes for this alternative title. One or more of the following: "alternative", "dvd", "festival", "tv", "video", "working", "original", "imdbDisplay". New values may be added in the future without warning
    attributes (array) - Additional terms to describe this alternative title, not enumerated
    isOriginalTitle (boolean) – 0: not original title; 1: original title
 */

@Getter
@Setter
@ToString
@NoArgsConstructor

@Entity

@NamedQueries(
        value = {
                @NamedQuery(name = "Aka.findByTitleId", query = "select a from Aka a where a.id.titleId = :input")
        }
)

public class Aka {

    @EmbeddedId
    private AkaId id;

    @Column(columnDefinition = "mediumtext")
    private String title;
    private String region;
    private String language;
    private String types;
    private String attributes;
    private boolean isOriginalTitle;

    public Aka(List<String> data) {
        this(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(5), data.get(6), data.get(7));
    }

    private Aka(String titleId,
                String ordering,
                String title,
                String region,
                String language,
                String types,
                String attributes,
                String isOriginalTitle) {

        this.id = new AkaId(titleId, Long.parseLong(ordering));

        this.title = title;
        this.region = region;
        this.language = language;
        this.types = types;
        this.attributes = attributes;
        this.isOriginalTitle = isOriginalTitle.equals("1");
    }

    public List<String> getTypesSplited() {
        return Collections.unmodifiableList(Arrays.asList(this.types.split(",")));
    }

    public List<String> getAttributesSplited() {
        return Collections.unmodifiableList(Arrays.asList(this.attributes.split(",")));
    }
}
