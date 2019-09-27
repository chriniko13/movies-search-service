package com.chriniko.lunatech.movies.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.List;

/*
    Contains the tv episode information. Fields include:

    tconst (string) - alphanumeric identifier of episode
    parentTconst (string) - alphanumeric identifier of the parent TV Series
    seasonNumber (integer) – season number the episode belongs to
    episodeNumber (integer) – episode number of the tconst in the TV series
 */

@Getter
@Setter
@ToString
@NoArgsConstructor

@Entity

@NamedQueries(
        value = {
                @NamedQuery(name = "Episode.findByParentTconst", query = "select e from Episode e where e.parentTconst = :input")
        }
)

public class Episode {

    @Id
    private String tconst;

    private String parentTconst;
    private Long seasonNumber;
    private Long episodeNumber;

    public Episode(List<String> data) {
        this(data.get(0), data.get(1), data.get(2), data.get(3));
    }

    private Episode(String tconst,
                    String parentTconst,
                    String seasonNumber,
                    String episodeNumber) {
        this.tconst = tconst;
        this.parentTconst = parentTconst;

        try {
            this.seasonNumber = Long.parseLong(seasonNumber);
        } catch (NumberFormatException error) {
            this.seasonNumber = null;
        }

        try {
            this.episodeNumber = Long.parseLong(episodeNumber);
        } catch (NumberFormatException error) {
            this.episodeNumber = null;
        }
    }
}
