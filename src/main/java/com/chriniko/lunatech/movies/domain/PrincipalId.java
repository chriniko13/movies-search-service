package com.chriniko.lunatech.movies.domain;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode

@Embeddable
public class PrincipalId implements Serializable {

    private String tconst;
    private Long ordering;
}
