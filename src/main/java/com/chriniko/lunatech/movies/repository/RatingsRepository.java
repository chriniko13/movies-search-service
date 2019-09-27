package com.chriniko.lunatech.movies.repository;

import com.chriniko.lunatech.movies.domain.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
public class RatingsRepository {

    private EntityManager entityManager;

    @Autowired
    public RatingsRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Rating> find(String tconst) {
        Rating fetchedRating = entityManager.find(Rating.class, tconst);
        return Optional.ofNullable(fetchedRating);
    }

}
