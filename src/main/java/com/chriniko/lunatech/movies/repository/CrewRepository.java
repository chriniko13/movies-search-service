package com.chriniko.lunatech.movies.repository;

import com.chriniko.lunatech.movies.domain.Crew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
public class CrewRepository {

    private final EntityManager entityManager;

    @Autowired
    public CrewRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Crew> find(String tconst) {
        Crew crew = entityManager.find(Crew.class, tconst);
        return Optional.ofNullable(crew);
    }
}
