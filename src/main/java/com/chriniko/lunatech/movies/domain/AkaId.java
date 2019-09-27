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
public class AkaId implements Serializable {

    private String titleId;
    private Long ordering;
}
