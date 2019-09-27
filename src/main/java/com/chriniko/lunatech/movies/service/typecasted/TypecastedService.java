package com.chriniko.lunatech.movies.service.typecasted;

import com.chriniko.lunatech.movies.dto.core.Title;
import com.chriniko.lunatech.movies.dto.search.name.TypecastedInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
    Note: a person becomes typecasted when at least half of their work is one genre.
 */

@Service
public class TypecastedService {

    public TypecastedInfo process(List<Title> titles) {

        Map<String, Long> genresPlayedGroupByCount = getGenresPlayedGroupByCount(titles);
        Long distinctSumOfGenres = getDistinctSumOfGenres(genresPlayedGroupByCount);

        long halfWork = titles.size() / 2;
        IsTypecastedResult isTypecastedResult = isTypecasted(genresPlayedGroupByCount, halfWork);

        return TypecastedInfo.builder()
                .isTypecasted(isTypecastedResult.typecasted)
                .genreTypecasted(isTypecastedResult.genreTypecasted)
                .halfWork(halfWork)
                .distinctSumOfGenres(distinctSumOfGenres)
                .genresPlayedGroupByCount(genresPlayedGroupByCount)
                .build();
    }

    private Map<String, Long> getGenresPlayedGroupByCount(List<Title> titles) {
        return titles.stream()
                .map(Title::getGenres)
                .map(genres -> Arrays.asList(genres.split(",")))
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private Long getDistinctSumOfGenres(Map<String, Long> genresPlayedGroupByCount) {
        return genresPlayedGroupByCount
                .entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .reduce(0L, Long::sum);
    }

    private IsTypecastedResult isTypecasted(Map<String, Long> genresPlayedGroupByCount, Long halfWork) {

        List<IsTypecastedResult> isTypecastedResults = genresPlayedGroupByCount
                .entrySet()
                .stream()
                .filter(e -> e.getValue() >= halfWork)
                .map(e -> new IsTypecastedResult(true, e.getKey()))
                .collect(Collectors.toList());


        if (isTypecastedResults.size() == 1) {
            return isTypecastedResults.get(0);
        } else if (isTypecastedResults.size() == 2) { // Note: there is an extreme such as, 200 action genre appearances and 200 drama genre appearances
            return new IsTypecastedResult(
                    true,
                    isTypecastedResults.stream().map(r -> r.genreTypecasted).collect(Collectors.joining(","))
            );
        } else {
            return new IsTypecastedResult(false, null);
        }
    }

    @RequiredArgsConstructor
    private class IsTypecastedResult {
        private final boolean typecasted;
        private final String genreTypecasted;
    }

}
