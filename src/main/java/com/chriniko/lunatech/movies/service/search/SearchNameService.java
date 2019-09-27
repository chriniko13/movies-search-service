package com.chriniko.lunatech.movies.service.search;

import com.chriniko.lunatech.movies.domain.Basic;
import com.chriniko.lunatech.movies.domain.Name;
import com.chriniko.lunatech.movies.dto.core.Person;
import com.chriniko.lunatech.movies.dto.core.Title;
import com.chriniko.lunatech.movies.dto.search.name.PersonGenreInfo;
import com.chriniko.lunatech.movies.dto.search.name.TypecastedInfo;
import com.chriniko.lunatech.movies.repository.BasicRepository;
import com.chriniko.lunatech.movies.repository.NamesRepository;
import com.chriniko.lunatech.movies.service.mapper.TitleMapperService;
import com.chriniko.lunatech.movies.service.typecasted.TypecastedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SearchNameService {

    @Autowired
    private BasicRepository basicRepository;

    @Autowired
    private NamesRepository namesRepository;

    @Autowired
    private TitleMapperService titleMapperService;

    @Autowired
    private TypecastedService typecastedService;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Optional<List<PersonGenreInfo>> byName(String name, boolean fullFetch) {

        final List<Name> names = namesRepository.findByPrimaryNameLike(name);

        final List<PersonGenreInfo> results = new ArrayList<>(names.size());

        for (Name n : names) {

            List<Basic> basics = basicRepository.findTitlesByNconst(n.getNconst());

            Person person = titleMapperService.map(n);

            List<Title> titles = getTitles(fullFetch, basics);

            TypecastedInfo typecastedInfo = typecastedService.process(titles);

            PersonGenreInfo personGenreInfo = PersonGenreInfo.builder()
                    .person(person)
                    .noOfTitles(titles.size())
                    .titles(titles)
                    .typecastedInfo(typecastedInfo)
                    .build();

            results.add(personGenreInfo);
        }

        return Optional.of(results);
    }

    private List<Title> getTitles(boolean fullFetch, List<Basic> basics) {
        return basics
                .stream()
                .map(b -> titleMapperService.map(b, fullFetch))
                .collect(Collectors.toList());
    }

}
