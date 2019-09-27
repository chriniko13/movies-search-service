package com.chriniko.lunatech.movies.dto.links;

import com.chriniko.lunatech.movies.domain.Name;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FindAcquaintanceLinksResult {

    private String resultStatus;

    private Name sourceName;
    private Name targetName;

    private long degrees;

    @Singular
    private List<Link> links;

}
