package com.chriniko.lunatech.movies.repository;

import com.chriniko.lunatech.movies.domain.Aka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class AkaRepository {

    private final EntityManager entityManager;

    @Autowired
    public AkaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Note: titleId == tconst
    public List<Aka> find(String titleId) {
        TypedQuery<Aka> q = entityManager.createNamedQuery("Aka.findByTitleId", Aka.class);
        q.setParameter("input", titleId);
        return q.getResultList();
    }
}
