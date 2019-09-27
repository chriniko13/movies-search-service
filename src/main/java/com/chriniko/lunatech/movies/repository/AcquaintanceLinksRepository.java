package com.chriniko.lunatech.movies.repository;

import io.vavr.control.Try;
import org.neo4j.driver.internal.value.PathValue;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.types.Path;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.stream.Stream;

@Repository
public class AcquaintanceLinksRepository {

    public Try<Path> getPath(String sourceNameNconst, String targetNameNconst, Transaction tx) {
        HashMap<String, Object> shortestPathParams = io.vavr.collection.HashMap.<String, Object>of(
                "source", sourceNameNconst,
                "target", targetNameNconst)
                .toJavaMap();

        return Try.ofSupplier(() -> {
            PathValue pathValue = (PathValue) tx
                    .run(
                            "MATCH path=shortestPath(" +
                                    "(name_Source:Name {nconst:$source})-[*0..6]-(name_Target:Name {nconst:$target})" +
                                    ") RETURN path",
                            shortestPathParams
                    )
                    .next()
                    .get("path");

            return pathValue.asPath();
        });

    }

    public void createNameNode(Transaction tx, String nconst, String primaryName) {
        HashMap<String, Object> createNameNodeParams = io.vavr.collection.HashMap.<String, Object>of(
                "nconst", nconst,
                "primaryName", primaryName)
                .toJavaMap();
        tx.run("CREATE (n:Name {nconst:$nconst, primaryName:$primaryName})",
                createNameNodeParams);
    }

    public void createBasicNode(Transaction tx, String tconst, String primaryTitle) {
        HashMap<String, Object> createBasicNodeParams = io.vavr.collection.HashMap.<String, Object>of(
                "tconst", tconst,
                "primaryTitle", primaryTitle)
                .toJavaMap();
        tx.run("CREATE (b:Basic {tconst:$tconst, primaryTitle:$primaryTitle})",
                createBasicNodeParams);
    }

    public boolean checkIfBasicNodeExists(Transaction tx, String tconst) {
        HashMap<String, Object> checkIfBasicExistsParams = io.vavr.collection.HashMap.<String, Object>of(
                "tconst", tconst)
                .toJavaMap();

        return tx
                .run("MATCH (b:Basic) WHERE b.tconst=$tconst RETURN b", checkIfBasicExistsParams)
                .hasNext();
    }

    public Boolean checkIfNameNodesExist(Transaction tx, String... nconsts) {
        return Stream.of(nconsts)
                .map(nconst -> checkIfNameNodeExists(tx, nconst))
                .reduce(true, (acc, elem) -> acc && elem);
    }

    public boolean checkIfNameNodeExists(Transaction tx, String nconst) {
        HashMap<String, Object> checkIfNameExistsParams = io.vavr.collection.HashMap.<String, Object>of(
                "nconst", nconst)
                .toJavaMap();

        return tx
                .run("MATCH (n:Name) WHERE n.nconst=$nconst RETURN n", checkIfNameExistsParams)
                .hasNext();
    }

    public void createActedRelationship(Transaction tx, String nconst, String tconst) {
        HashMap<String, Object> createRelationshipParams = io.vavr.collection.HashMap.<String, Object>of(
                "nconst", nconst,
                "tconst", tconst)
                .toJavaMap();

        tx.run(
                "MATCH (n:Name), (b:Basic) " +
                        "WHERE n.nconst=$nconst AND b.tconst=$tconst " +
                        "CREATE (n)-[r:ACTED]->(b) " +
                        "RETURN n,b",
                createRelationshipParams
        );
    }

    public void createAppearedRelationship(Transaction tx, String nconst, String tconst) {
        HashMap<String, Object> createRelationshipParams = io.vavr.collection.HashMap.<String, Object>of(
                "nconst", nconst,
                "tconst", tconst)
                .toJavaMap();

        tx.run(
                "MATCH (b:Basic), (n:Name) " +
                        "WHERE b.tconst=$tconst AND n.nconst=$nconst " +
                        "CREATE (b)-[r:APPEARED]->(n) " +
                        "RETURN n,b",
                createRelationshipParams
        );
    }

}
