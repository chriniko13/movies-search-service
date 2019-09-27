package com.chriniko.lunatech.movies.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/*
    Contains the IMDb rating and votes information for titles

    tconst (string) - alphanumeric unique identifier of the title
    averageRating – weighted average of all the individual user ratings
    numVotes - number of votes the title has received
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Rating {

    @Id
    private String tConst;

    private Double averageRating;
    private Long numVotes;

    public Rating(List<String> data) {
        this(data.get(0), data.get(1), data.get(2));
    }

    private Rating(String tConst, String averageRating, String numVotes) {
        this.tConst = tConst;

        try {
            this.averageRating = Double.parseDouble(averageRating);
        } catch (NumberFormatException error) {
            this.averageRating = null;
        }

        try {
            this.numVotes = Long.parseLong(numVotes);
        } catch (NumberFormatException error) {
            this.numVotes = null;
        }
    }
}
