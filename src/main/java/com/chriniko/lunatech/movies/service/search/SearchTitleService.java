package com.chriniko.lunatech.movies.service.search;

import com.chriniko.lunatech.movies.domain.Basic;
import com.chriniko.lunatech.movies.dto.core.Title;
import com.chriniko.lunatech.movies.error.ProcessingException;
import com.chriniko.lunatech.movies.repository.BasicRepository;
import com.chriniko.lunatech.movies.service.mapper.TitleMapperService;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
@Service
public class SearchTitleService {

    @Value("${search.max.results.to-process-strategy.enabled}")
    private boolean searchMaxResultsToProcessStrategyEnabled;

    @Value("${search.max.results.to-process}")
    private int searchMaxResultsToProcess;

    @Autowired
    private BasicRepository basicRepository;

    @Autowired
    private TitleMapperService titleMapperService;

    @Autowired
    private ThreadPoolExecutor workers;

    @Value("${search-by-title.multithread-approach.enabled}")
    private boolean multithreadApproach;

    @Value("${search-by-title.results-sublist-size}")
    private int sublistSize;

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            readOnly = true
    )
    public Optional<List<Title>> byTitle(String title, boolean fullFetch) {
        List<Basic> results = basicRepository.findByTitle(title);
        if (results.isEmpty()) {
            return Optional.empty();
        }

        if (searchMaxResultsToProcessStrategyEnabled
                && results.size() > searchMaxResultsToProcess) {
            results = results.subList(0, searchMaxResultsToProcess);
        }

        return multithreadApproach
                ? Optional.of(getTitlesMultiWorkers(fullFetch, results))
                : Optional.of(getTitlesSingleWorker(fullFetch, results));
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            readOnly = true
    )
    public Optional<List<Title>> byGenre(String genre, boolean fullFetch) {

        List<Basic> results = basicRepository.findXTopRatedTitlesByGenre(genre);

        List<Title> titles = results
                .stream()
                .map(r -> titleMapperService.map(r, fullFetch))
                .collect(Collectors.toList());

        return Optional.of(titles);
    }

    private List<Title> getTitlesSingleWorker(boolean fullFetch, List<Basic> results) {

        final List<Title> titles = new ArrayList<>(results.size());
        for (Basic result : results) {
            Title record = titleMapperService.map(result, fullFetch);
            titles.add(record);
        }
        return titles;
    }

    private List<Title> getTitlesMultiWorkers(boolean fullFetch, List<Basic> results) {
        final List<List<Basic>> partitionedResults = Lists.partition(results, sublistSize);

        return partitionedResults
                .stream()
                .map(partitionedResult -> {
                    return CompletableFuture.supplyAsync(
                            () -> {
                                return partitionedResult
                                        .stream()
                                        .map(r -> titleMapperService.mapPerThread(r, fullFetch))
                                        .collect(Collectors.toList());
                            },
                            workers);
                })
                .collect(Collectors.toList())
                .stream()
                .map(listCompletableFuture -> {
                    try {
                        return listCompletableFuture.get(45, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "could not map title, error: " + e.getMessage(), e);
                        throw new ProcessingException(e);
                    }
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
