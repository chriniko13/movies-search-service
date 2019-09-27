package com.chriniko.lunatech.movies.repository;

import com.chriniko.lunatech.movies.domain.Basic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BasicRepository {

    @Value("${records.limit.on-top-rated-genre}")
    private int recordsLimitOnTopRatedGenre;

    private final EntityManager entityManager;

    @Autowired
    public BasicRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Basic> findByTitle(String title) {
        TypedQuery<Basic> q1 = entityManager.createNamedQuery("Basic.searchByPrimaryTitle", Basic.class);
        q1.setParameter("input", title + "%");
        List<Basic> resultsForSearchByPrimaryTitle = q1.getResultList();

        TypedQuery<Basic> q2 = entityManager.createNamedQuery("Basic.searchByOriginalTitle", Basic.class);
        q2.setParameter("input", title + "%");
        List<Basic> resultsForSearchByOriginalTitle = q2.getResultList();

        Set<Basic> deduplication = new HashSet<>();
        deduplication.addAll(resultsForSearchByPrimaryTitle);
        deduplication.addAll(resultsForSearchByOriginalTitle);

        return new ArrayList<>(deduplication);
    }

    public List<Basic> findXTopRatedTitlesByGenre(String genre) {
        TypedQuery<String> q1 = entityManager.createNamedQuery("Basic.findXTopRatedTitleIdsByGenre", String.class);
        q1.setParameter("input", "%" + genre + "%");

        List<String> tconsts = q1.setMaxResults(recordsLimitOnTopRatedGenre).getResultList();

        TypedQuery<Basic> q2 = entityManager.createNamedQuery("Basic.findByTconsts", Basic.class);
        q2.setParameter("tconsts", tconsts);

        return q2.getResultList();
    }

    public List<Basic> findTitlesByNconst(String nconst) {
        TypedQuery<Basic> q = entityManager.createNamedQuery("Basic.findTitlesByNconst", Basic.class);
        q.setParameter("input", nconst);
        return q.getResultList();
    }

    public List<Object[]> findTitlesByNconstBasicInfoOnlyActorCategory(String nconst) {
        TypedQuery<Object[]> q = entityManager.createNamedQuery("Basic.findTitlesByNconstBasicInfo", Object[].class);
        q.setParameter("input", nconst);
        return q.getResultList();
    }

    public List<Basic> findTitlesByNconst(List<String> nconsts) {
        TypedQuery<Basic> q = entityManager.createNamedQuery("Basic.findTitlesByNconsts", Basic.class);
        q.setParameter("input", nconsts);
        return q.getResultList();
    }

    public Set<String> getGenres() {
        TypedQuery<String> q = entityManager.createNamedQuery("Basic.getAllGenresDistinct", String.class);

        List<String> genresSomeConcatenated = q.getResultList();

        return genresSomeConcatenated
                .stream()
                .filter(g -> g.contains(","))
                .map(g -> Arrays.asList(g.split(",")))
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }
}
