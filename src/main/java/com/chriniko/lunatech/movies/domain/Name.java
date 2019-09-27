package com.chriniko.lunatech.movies.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
    Contains the following information for names:

    nconst (string) - alphanumeric unique identifier of the name/person
    primaryName (string)– name by which the person is most often credited
    birthYear – in YYYY format
    deathYear – in YYYY format if applicable, else '\N'
    primaryProfession (array of strings)– the top-3 professions of the person
    knownForTitles (array of tconsts) – titles the person is known for
 */

@Getter
@Setter
@ToString
@NoArgsConstructor

@Entity

@NamedQueries(
        value = {
                @NamedQuery(
                        name = "Name.findByPrimaryNameLike",
                        query = "select n from Name n where n.primaryName like :input"
                ),
                @NamedQuery(
                        name = "Name.findByPrimaryName",
                        query = "select n from Name n where n.primaryName = :input"
                ),
                @NamedQuery(
                        name = "Name.findTotalRecords",
                        query = "select count(n.nconst) from Name n"
                ),
                @NamedQuery(
                        name = "Name.selectBasicInfo",
                        query = "select n.nconst, n.primaryName from Name n"
                )
        }
)

@EqualsAndHashCode(of = {"nconst"})

public class Name {


    @Id
    private String nconst;

    private String primaryName;
    private String birthYear;
    private String deathYear;
    private String primaryProfessions;
    private String knownForTitles;

    public Name(List<String> data) {
        this(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(5));
    }

    private Name(String nconst,
                 String primaryName,
                 String birthYear,
                 String deathYear,
                 String primaryProfessions,
                 String knownForTitles) {

        this.nconst = nconst;
        this.primaryName = primaryName;
        this.birthYear = birthYear;
        this.deathYear = deathYear;
        this.primaryProfessions = primaryProfessions;
        this.knownForTitles = knownForTitles;
    }

    public List<String> getPrimaryProfessionsSplited() {
        return Collections.unmodifiableList(Arrays.asList(this.primaryProfessions.split(",")));
    }

    public List<String> getKnownForTitlesSplited() {
        return Collections.unmodifiableList(Arrays.asList(this.knownForTitles.split(",")));
    }
}
