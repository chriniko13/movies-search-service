package com.chriniko.lunatech.movies.resource;

import com.chriniko.lunatech.movies.core.Urls;
import com.chriniko.lunatech.movies.dto.search.title.SearchTitleResults;
import com.chriniko.lunatech.movies.dto.core.Title;
import com.chriniko.lunatech.movies.error.ValidationException;
import com.chriniko.lunatech.movies.service.search.SearchTitleService;
import com.chriniko.lunatech.movies.validator.SearchByGenreValidator;
import com.chriniko.lunatech.movies.validator.SearchByTitleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("search/titles")
public class SearchTitleResource {

    private final SearchTitleService searchTitleService;
    private final SearchByTitleValidator searchByTitleValidator;
    private final SearchByGenreValidator searchByGenreValidator;
    private final Urls urls;

    @Autowired
    public SearchTitleResource(SearchTitleService searchTitleService,
                               SearchByTitleValidator searchByTitleValidator,
                               SearchByGenreValidator searchByGenreValidator,
                               Urls urls) {
        this.searchTitleService = searchTitleService;
        this.searchByTitleValidator = searchByTitleValidator;
        this.searchByGenreValidator = searchByGenreValidator;
        this.urls = urls;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public HttpEntity<SearchTitleResults> searchBy(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "full-fetch", defaultValue = "false") boolean fullFetch) {

        Optional<List<Title>> searchResults;
        if (query.contains("title")) {

            String title = query.split(":")[1]; //TODO fix
            title = urls.decoder(title);

            searchByTitleValidator.test(title);
            searchResults = searchTitleService.byTitle(title, fullFetch);

        } else if (query.contains("genre")) {

            String genre = query.split(":")[1];
            genre = urls.decoder(genre);

            // capitalize first letter of provided genre if necessary.
            if (Character.isLowerCase(genre.charAt(0))) {
                genre = genre.substring(0, 1).toUpperCase() + genre.substring(1);
            }

            searchByGenreValidator.test(genre);
            searchResults = searchTitleService.byGenre(genre, fullFetch);

        } else {
            throw new ValidationException("not valid provided query");
        }

        List<Title> fetchedTitles = searchResults
                .filter(l -> !l.isEmpty())
                .orElseGet(ArrayList::new);

        return ResponseEntity.ok(new SearchTitleResults(query, fetchedTitles.size(), fetchedTitles));
    }

}
