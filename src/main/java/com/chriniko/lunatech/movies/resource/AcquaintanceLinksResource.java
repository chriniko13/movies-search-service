package com.chriniko.lunatech.movies.resource;


import com.chriniko.lunatech.movies.dto.links.FindAcquaintanceLinks;
import com.chriniko.lunatech.movies.dto.links.FindAcquaintanceLinksPrepareSelectedNames;
import com.chriniko.lunatech.movies.dto.links.FindAcquaintanceLinksResult;
import com.chriniko.lunatech.movies.dto.links.FindAcquaintanceLinksResults;
import com.chriniko.lunatech.movies.service.links.AcquaintanceLinksService;
import com.chriniko.lunatech.movies.validator.AcquaintanceLinksValidator;
import io.vavr.Tuple;
import io.vavr.collection.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("acquaintance-links")
public class AcquaintanceLinksResource {

    @Autowired
    private AcquaintanceLinksService acquaintanceLinksService;

    @Autowired
    private ThreadPoolExecutor workers;

    @Autowired
    private AcquaintanceLinksValidator acquaintanceLinksValidator;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public HttpEntity<FindAcquaintanceLinksResults> search(@RequestBody FindAcquaintanceLinks findAcquaintanceLinks) {

        String sourceFullName = findAcquaintanceLinks.getSourceFullName();
        String targetFullName = findAcquaintanceLinks.getTargetFullName();

        Stream.of(
                        Tuple.of("sourceFullName", sourceFullName),
                        Tuple.of("targetFullName", targetFullName)
                )
                .forEach(t -> acquaintanceLinksValidator.test(t._1, t._2));

        List<FindAcquaintanceLinksResult> results = acquaintanceLinksService.search(sourceFullName, targetFullName);

        return ResponseEntity.ok(new FindAcquaintanceLinksResults(sourceFullName, targetFullName, results));
    }

    @RequestMapping(
            path = "prepare",
            method = RequestMethod.POST
    )
    public HttpEntity<Void> prepare() {
        workers.submit(() -> acquaintanceLinksService.prepareNeo4jDbFromMySql());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            path = "prepare",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public HttpEntity<Void> prepare(@RequestBody FindAcquaintanceLinksPrepareSelectedNames prepareSelectedNames) {

        List<String> names = prepareSelectedNames.getNames();
        acquaintanceLinksValidator.test(names);

        workers.submit(() -> acquaintanceLinksService.prepareNeo4jDbFromMySql(names));
        return ResponseEntity.ok().build();
    }

}
