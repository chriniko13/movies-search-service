package com.chriniko.lunatech.movies.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

/*
    Contains the following information for titles:

    tconst (string) - alphanumeric unique identifier of the title
    titleType (string) – the type/format of the title (e.g. movie, short, tvseries, tvepisode, video, etc)
    primaryTitle (string) – the more popular title / the title used by the filmmakers on promotional materials at the point of release
    originalTitle (string) - original title, in the original language
    isAdult (boolean) - 0: non-adult title; 1: adult title
    startYear (YYYY) – represents the release year of a title. In the case of TV Series, it is the series start year
    endYear (YYYY) – TV Series end year. ‘\N’ for all other title types
    runtimeMinutes – primary runtime of the title, in minutes
    genres (string array) – includes up to three genres associated with the title

 */
@Getter
@Setter
@ToString
@NoArgsConstructor

@EqualsAndHashCode(of = {"tconst"})

@Entity

@NamedQueries(
        value = {
                @NamedQuery(
                        name = "Basic.searchByPrimaryTitle",
                        query = "select b from Basic b where b.primaryTitle like :input"),
                @NamedQuery(
                        name = "Basic.searchByOriginalTitle",
                        query = "select b from Basic b where b.originalTitle like :input"),
                @NamedQuery(
                        name = "Basic.getAllGenresDistinct",
                        query = "select distinct(b.genres) from Basic b"
                ),
                @NamedQuery(
                        name = "Basic.findXTopRatedTitleIdsByGenre",
                        query = "select r.tConst from Rating r where r.tConst in (select b.tconst from Basic b where b.genres like :input) order by r.averageRating desc"
                ),
                @NamedQuery(
                        name = "Basic.findByTconsts",
                        query = "select b from Basic b where b.tconst in :tconsts"
                ),
                @NamedQuery(
                        name = "Basic.findTitlesByNconst",
                        query = "select b from Basic b where b.tconst in (select p.id.tconst from Principal p where p.nconst = :input)"
                ),
                @NamedQuery(
                        name = "Basic.findTitlesByNconstBasicInfo",
                        query = "select b.tconst, b.primaryTitle " +
                                "from Basic b " +
                                "where b.tconst in (select p.id.tconst from Principal p where p.nconst = :input and p.category in ('actor', 'actress'))"
                ),
                @NamedQuery(
                        name = "Basic.findTitlesByNconsts",
                        query = "select b from Basic b where b.tconst in (select p.id.tconst from Principal p where p.nconst in :input)"
                )

        }
)

public class Basic {

    @Id
    private String tconst;

    private String titleType;
    @Column(length = 500)
    private String primaryTitle;
    @Column(length = 500)
    private String originalTitle;
    private boolean isAdult;
    private String startYear;
    private String endYear;
    private Integer runtimeMinutes;
    private String genres;

    public Basic(List<String> data) {
        this(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(5), data.get(6), data.get(7), data.get(8));
    }

    private Basic(String tconst,
                  String titleType,
                  String primaryTitle,
                  String originalTitle,
                  String isAdult,
                  String startYear,
                  String endYear,
                  String runtimeMinutes,
                  String genres) {
        this.tconst = tconst;
        this.titleType = titleType;
        this.primaryTitle = primaryTitle;
        this.originalTitle = originalTitle;
        this.isAdult = isAdult.equals("1");
        this.startYear = startYear;
        this.endYear = endYear;

        try {
            this.runtimeMinutes = Integer.parseInt(runtimeMinutes);
        } catch (NumberFormatException error) {
            this.runtimeMinutes = null;
        }

        this.genres = genres;
    }

}
