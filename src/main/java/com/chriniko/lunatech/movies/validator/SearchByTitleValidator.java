package com.chriniko.lunatech.movies.validator;

import com.chriniko.lunatech.movies.error.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class SearchByTitleValidator {

    public void test(String title) {
        if (title.length() < 5) {
            throw new ValidationException("title should be at least 5 characters");
        }
    }
}
