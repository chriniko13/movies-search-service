package com.chriniko.lunatech.movies.service.links;

import com.chriniko.lunatech.movies.domain.Name;
import com.chriniko.lunatech.movies.dto.links.FindAcquaintanceLinksResult;
import com.chriniko.lunatech.movies.dto.links.Link;
import com.chriniko.lunatech.movies.repository.AcquaintanceLinksRepository;
import com.chriniko.lunatech.movies.repository.BasicRepository;
import com.chriniko.lunatech.movies.repository.NamesRepository;
import com.google.common.collect.Lists;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.extern.java.Log;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/*

Quick Notes for Neo4j:

    SELECT =>                            match  (n:Name) where n.primaryName = 'Lauren Bacall' return n limit 10

    INSERT =>                            CREATE (n:Name {nconst:$nconst, primaryName:$primaryName})

    COUNT =>                             match  (n:Name) return count(*)

    SEE INDEXES =>                       CALL db.indexes
    CREATE INDEX =>                      create index on :Name(nconst)

    FIND MOVIES OF ACTOR (RELATIONS) =>  MATCH p=(n:Name {primaryName:'Brad Pitt'})-[r:ACTED]->(b:Basic) RETURN p LIMIT 100
    FIND MOVIES OF ACTOR             =>  MATCH p=(n:Name {primaryName:'Brad Pitt'})-[r:ACTED]->(b:Basic) RETURN b LIMIT 100

    FIND ACTORS OF MOVIE (RELATIONS) =>  MATCH p=(b:Basic {primaryTitle:'Shall We Dance'})-[r:APPEARED]->(n:Name) RETURN p LIMIT 100
    FIND ACTORS OF MOVIE             =>  MATCH p=(b:Basic {primaryTitle:'Shall We Dance'})-[r:APPEARED]->(n:Name) RETURN n LIMIT 100

    SHORTEST PATH 1 =>     MATCH path=shortestPath((name_JenniferLopez:Name {nconst:'nm0000182'})-[*0..10]-(name_RichardGere:Name {nconst:'nm0000152'}))
                           RETURN path

    SHORTEST PATH 2 =>     MATCH path=shortestPath((name_BradPitt:Name {nconst:'nm0000093'})-[*0..10]-(name_RichardGere:Name {nconst:'nm0000152'}))
                           RETURN path

    SHORTEST PATH 3 =>     MATCH path=shortestPath((name_BradPitt:Name {nconst:'nm0000093'})-[*0..10]-(name_KevinBacon:Name {nconst:'nm0000102'}))
                           RETURN path

    SHORTEST PATH 4 =>     MATCH path=shortestPath((name_MattDamon:Name {nconst:'nm0000354'})-[*0..10]-(name_HenryFonda:Name {nconst:'nm0000020'}))
                           RETURN path
 */

@Log
@Service
public class AcquaintanceLinksService {

    private static final String GET_LINKS_OPERATION_OK_MSG = "Get links operation completed successfully.";
    private static final String GET_LINKS_OPERATION_NOT_OK_MSG_1 = "Get links operation could not applied, due to missing required Name record(s)/node(s).";
    private static final String GET_LINKS_OPERATION_NOT_OK_MSG_2 = "Could not find path (eg: more than six degrees).";

    @Autowired
    private NamesRepository namesRepository;

    @Autowired
    private BasicRepository basicRepository;

    @Autowired
    private AcquaintanceLinksRepository acquaintanceLinksRepository;

    private static final String NEO4J_URL = "bolt://localhost:7687";
    private static final String NEO4J_USERNAME = "neo4j";
    private static final String NEO4J_PASSWORD = "1234";

    private static final int PROCESS_BATCH_SIZE = 50;

    private Driver getDriver() {
        return GraphDatabase.driver(
                NEO4J_URL, AuthTokens.basic(NEO4J_USERNAME, NEO4J_PASSWORD));
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<FindAcquaintanceLinksResult> search(String sourceFullName, String targetFullName) {

        final List<FindAcquaintanceLinksResult> results = new LinkedList<>();

        try (Driver driver = getDriver();
             Session session = driver.session()) {

            List<Name> sourceNames = namesRepository.findByPrimaryName(sourceFullName);
            List<Name> targetNames = namesRepository.findByPrimaryName(targetFullName);

            for (Name sourceName : sourceNames) {
                for (Name targetName : targetNames) {

                    String sourceNameNconst = sourceName.getNconst();
                    String targetNameNconst = targetName.getNconst();

                    FindAcquaintanceLinksResult.FindAcquaintanceLinksResultBuilder findAcquaintanceLinksResultBuilder
                            = FindAcquaintanceLinksResult.builder()
                            .sourceName(sourceName)
                            .targetName(targetName);

                    try (Transaction tx = session.beginTransaction()) {

                        Boolean allNodesExist = acquaintanceLinksRepository.checkIfNameNodesExist(
                                tx, sourceNameNconst, targetNameNconst);

                        if (!allNodesExist) {
                            findAcquaintanceLinksResultBuilder
                                    .resultStatus(GET_LINKS_OPERATION_NOT_OK_MSG_1);
                        } else {

                            Try<Path> pathOperationResult
                                    = acquaintanceLinksRepository.getPath(sourceNameNconst, targetNameNconst, tx);

                            pathOperationResult
                                    .onSuccess(path -> {
                                        Tuple2<Long, List<Link>> links = getLinks(path);

                                        findAcquaintanceLinksResultBuilder
                                                .resultStatus(GET_LINKS_OPERATION_OK_MSG)
                                                .degrees(links._1)
                                                .links(links._2);
                                    })
                                    .onFailure(error -> {
                                        findAcquaintanceLinksResultBuilder
                                                .resultStatus(GET_LINKS_OPERATION_NOT_OK_MSG_2);
                                    });
                        }

                        results.add(findAcquaintanceLinksResultBuilder.build());
                    }
                }
            }
        }

        return results;
    }

    private Tuple2<Long, List<Link>> getLinks(Path path) {

        List<Node> nodes = Lists.newArrayList(path.nodes());
        List<Relationship> relationships = Lists.newArrayList(path.relationships());

        List<Link> links = new ArrayList<>(nodes.size() + relationships.size());

        long degrees = 0L;

        for (int i = 0; i < nodes.size(); i++) {
            boolean isLastNode = i == nodes.size() - 1;

            Node node = nodes.get(i);
            String nodeType = node.labels().iterator().next();

            links.add(Link
                    .builder()
                    .type(nodeType)
                    .properties(node.asMap())
                    .build());

            if ("Basic".equals(nodeType)) {
                degrees++;
            }

            if (!isLastNode) {
                Relationship relationship = relationships.get(i);
                links.add(Link
                        .builder()
                        .type(relationship.type())
                        .build());
            }
        }

        return Tuple.of(degrees, links);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void prepareNeo4jDbFromMySql() {
        try (Driver driver = getDriver();
             Session session = driver.session()) {

            final Long totalNameRecords = namesRepository.getTotalRecords();
            final long steps = totalNameRecords / PROCESS_BATCH_SIZE;

            log.log(Level.INFO,
                    "AcquaintanceLinksService#prepareNeo4jDbFromMySql, total steps to execute: " + steps);

            int offset = 0;
            long startTimeInNs = System.nanoTime();

            for (int i = 0; i < steps; i++, offset += PROCESS_BATCH_SIZE) {

                log.log(Level.INFO,
                        "AcquaintanceLinksService#prepareNeo4jDbFromMySql, currently executing step: " + (i + 1) + "/" + steps);

                List<Object[]> results = namesRepository.selectNconstAndPrimaryName(offset, PROCESS_BATCH_SIZE);
                for (Object[] result : results) {

                    String nconst = (String) result[0];
                    String primaryName = (String) result[1];

                    createNodesAndRelations(session, nconst, primaryName);
                }
            }

            long totalTimeInNs = System.nanoTime() - startTimeInNs;
            long totalTimeInMinutes = TimeUnit.MINUTES.convert(totalTimeInNs, TimeUnit.NANOSECONDS);
            log.log(Level.INFO,
                    "AcquaintanceLinksService#prepareNeo4jDbFromMySql, total time to prepare Neo4J in minutes: " + totalTimeInMinutes);

        }
    }

    private void createNodesAndRelations(Session session, String nconst, String primaryName) {
        try (Transaction tx = session.beginTransaction()) {

            if (!acquaintanceLinksRepository.checkIfNameNodesExist(tx, nconst)) {

                // Note: create name node...
                acquaintanceLinksRepository.createNameNode(tx, nconst, primaryName);

                // Note: now for each name node create all the movies which has participated and create the relationships also...
                List<Object[]> basics = basicRepository.findTitlesByNconstBasicInfoOnlyActorCategory(nconst);
                basics.forEach(basic -> {

                    String tconst = (String) basic[0];
                    String primaryTitle = (String) basic[1];

                    boolean basicNodeExists = acquaintanceLinksRepository.checkIfBasicNodeExists(tx, tconst);
                    if (!basicNodeExists) {
                        acquaintanceLinksRepository.createBasicNode(tx, tconst, primaryTitle);
                    }

                    acquaintanceLinksRepository.createActedRelationship(tx, nconst, tconst);
                    acquaintanceLinksRepository.createAppearedRelationship(tx, nconst, tconst);

                });

            }
            tx.success();
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void prepareNeo4jDbFromMySql(List<String> names) {

        try (Driver driver = getDriver();
             Session session = driver.session()) {

            for (String name : names) {
                List<Name> storedNames = namesRepository.findByPrimaryName(name);

                for (Name storedNamed : storedNames) {

                    String nconst = storedNamed.getNconst();
                    String primaryName = storedNamed.getPrimaryName();

                    createNodesAndRelations(session, nconst, primaryName);
                }
            }
        }
    }
}
