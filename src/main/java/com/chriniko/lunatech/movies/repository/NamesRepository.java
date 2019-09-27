package com.chriniko.lunatech.movies.repository;

import com.chriniko.lunatech.movies.domain.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class NamesRepository {

    private EntityManager entityManager;

    @Autowired
    public NamesRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Name> find(String nconst) {
        Name fetchedName = entityManager.find(Name.class, nconst);
        return Optional.ofNullable(fetchedName);
    }

    public List<Name> findByPrimaryNameLike(String primaryName) {
        TypedQuery<Name> q = entityManager.createNamedQuery("Name.findByPrimaryNameLike", Name.class);
        q.setParameter("input", primaryName + "%");
        return q.getResultList();
    }

    public List<Name> findByPrimaryName(String primaryName) {
        TypedQuery<Name> q = entityManager.createNamedQuery("Name.findByPrimaryName", Name.class);
        q.setParameter("input", primaryName);
        return q.getResultList();
    }

    public Long getTotalRecords() {
        return entityManager
                .createNamedQuery("Name.findTotalRecords", Long.class)
                .getSingleResult();
    }

    public List<Object[]> selectNconstAndPrimaryName(int offset, int maxResults) {

        TypedQuery<Object[]> namesImportantInfo = entityManager.createNamedQuery(
                "Name.selectBasicInfo",
                Object[].class);

        namesImportantInfo.setFirstResult(offset);
        namesImportantInfo.setMaxResults(maxResults);

        return namesImportantInfo.getResultList();
    }
}
