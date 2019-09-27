package com.chriniko.lunatech.movies.validator;

import com.chriniko.lunatech.movies.error.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AcquaintanceLinksValidator {



    public void test(String label, String name) {

        if (name == null) {
            throw new ValidationException(label + " should have a correct value, eg: John Doe");
        }

        if (!name.contains(" ")) {
            throw new ValidationException(label + " should have a space between firstname and surname, eg: John Doe");
        }

        if (name.length() < 8) {
            throw new ValidationException(label + " should be at least 8 characters");
        }
    }

    public void test(List<String> names) {
        names.stream()
             .filter(name -> name == null || name.isEmpty() || name.length() < 8)
             .findAny()
             .ifPresent(s -> {
                 throw new ValidationException("please enter a valid name");
             });
    }
}
