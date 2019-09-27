package com.chriniko.lunatech.movies.validator;

import com.chriniko.lunatech.movies.error.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
public class SearchByNameValidator {

    private Pattern pattern;

    public SearchByNameValidator() {
        pattern = Pattern.compile("^[\\w]{4,} [\\w]{4,}$");
    }

    public void test(String name) {
        if (!pattern.asPredicate().test(name)) {
            throw new ValidationException("name should have the following format, eg: John Doe, and should be at least 8 characters.");
        }
    }

    public void test(List<String> requestedNames) {

        Predicate<List<String>> isNull = Objects::isNull;
        Predicate<List<String>> notAtLeastTwoNames = names -> names.size() < 2;

        boolean validationNotSatisfied = isNull.or(notAtLeastTwoNames).test(requestedNames);

        if (validationNotSatisfied) {
            throw new ValidationException("please provide at least two full names");
        }
    }
}
