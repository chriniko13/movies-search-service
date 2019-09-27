package com.chriniko.lunatech.movies.repository;

import com.chriniko.lunatech.movies.domain.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class PrincipalsRepository {

    private final EntityManager entityManager;

    @Autowired
    public PrincipalsRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Principal> find(String tconst) {
        TypedQuery<Principal> q = entityManager.createNamedQuery("Principal.findByTconst", Principal.class);
        q.setParameter("input", tconst);
        return q.getResultList();
    }
}
