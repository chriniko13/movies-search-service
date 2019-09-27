package com.chriniko.lunatech.movies.repository;

import com.chriniko.lunatech.movies.domain.Episode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class EpisodesRepository {

    private final EntityManager entityManager;

    @Autowired
    public EpisodesRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Note: parentTconst == Basic.tconst
    public List<Episode> find(String parentTconst) {
        TypedQuery<Episode> q = entityManager.createNamedQuery("Episode.findByParentTconst", Episode.class);
        q.setParameter("input", parentTconst);
        return q.getResultList();
    }
}
