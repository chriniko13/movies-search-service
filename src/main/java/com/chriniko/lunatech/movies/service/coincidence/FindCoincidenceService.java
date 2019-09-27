package com.chriniko.lunatech.movies.service.coincidence;

import com.chriniko.lunatech.movies.domain.Basic;
import com.chriniko.lunatech.movies.domain.Name;
import com.chriniko.lunatech.movies.dto.coincidence.FindCoincidenceResult;
import com.chriniko.lunatech.movies.dto.core.Title;
import com.chriniko.lunatech.movies.repository.BasicRepository;
import com.chriniko.lunatech.movies.repository.NamesRepository;
import com.chriniko.lunatech.movies.service.mapper.TitleMapperService;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/*
    Note: Find the coincidence: Given a query by the user, where the input is two actors/actresses names,
          the application replies with a list of movies or TV shows that both people have shared.
 */

@Service
public class FindCoincidenceService {

    @Autowired
    private NamesRepository namesRepository;

    @Autowired
    private BasicRepository basicRepository;

    @Autowired
    private TitleMapperService titleMapperService;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Optional<FindCoincidenceResult> search(List<String> names) {

        FindCoincidenceResult.FindCoincidenceResultBuilder findCoincidenceResultBuilder = FindCoincidenceResult
                .builder()
                .executedForNames(names);

        final List<Tuple3<String, List<Name>, List<Title>>> tempBrainMemory = calculateTempBrainMemory(names);

        // Note: for multiple sets intersection.
        final List<Set<Title>> allTitles = new ArrayList<>();

        for (Tuple3<String, List<Name>, List<Title>> tempBrainRecord : tempBrainMemory) {
            findCoincidenceResultBuilder.names(tempBrainRecord._2);
            allTitles.add(new HashSet<>(tempBrainRecord._3));
        }

        Set<Title> keeper = allTitles.get(0);
        for (int i = 1; i < allTitles.size(); i++) {
            keeper.retainAll(allTitles.get(i));
        }

        FindCoincidenceResult result = findCoincidenceResultBuilder
                .noOfSharedTitles(keeper.size())
                .sharedTitles(keeper)
                .build();

        return Optional.of(result);
    }


    private List<Tuple3<String, List<Name>, List<Title>>> calculateTempBrainMemory(List<String> names) {

        final List<Tuple3<String, List<Name>, List<Title>>> tempBrainMemory = new ArrayList<>();
        for (String name : names) {

            List<Name> fetchedNames = namesRepository.findByPrimaryName(name);

            List<String> nconsts = fetchedNames
                    .stream()
                    .map(Name::getNconst)
                    .collect(Collectors.toList());

            List<Basic> basicsByPrimaryName = basicRepository.findTitlesByNconst(nconsts);

            List<Title> titlesByPrimaryName = basicsByPrimaryName
                    .stream()
                    .map(t -> titleMapperService.map(t, false))
                    .collect(Collectors.toList());

            tempBrainMemory.add(Tuple.of(name, fetchedNames, titlesByPrimaryName));
        }
        return tempBrainMemory;
    }
}
