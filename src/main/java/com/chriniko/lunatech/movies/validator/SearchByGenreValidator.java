package com.chriniko.lunatech.movies.validator;

import com.chriniko.lunatech.movies.error.ValidationException;
import com.chriniko.lunatech.movies.repository.BasicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Set;

@Component
public class SearchByGenreValidator {

    @Autowired
    private BasicRepository basicRepository;

    private Set<String> genres;

    @PostConstruct
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    void init() {
        genres = basicRepository.getGenres();
    }

    public void test(String genre) {
        if (!genres.contains(genre)) {
            throw new ValidationException("not valid genre");
        }
    }

}
