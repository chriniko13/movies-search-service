package com.chriniko.lunatech.movies.it

import com.chriniko.lunatech.movies.MoviesSearchServiceApplication
import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.skyscreamer.jsonassert.Customization
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.skyscreamer.jsonassert.comparator.CustomComparator
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpMethod
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(
        loader = SpringBootContextLoader.class,
        classes = MoviesSearchServiceApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class Requirement2_ITSpec extends Specification {

    @LocalServerPort
    protected Integer apiPort

    protected RestTemplate restTemplate;

    def setup() {
        restTemplate = new RestTemplate();
    }

    /*
        Scenario Description:

        Top rated movies: Given a query by the user, you must provide what are the top rated movies
        for a genre (If the user searches horror, then it should show a list of top rated horror movies).

     */

    @Unroll("fullFetch = #fullFetch, expectedOutcome = #expectedOutcome")
    def 'requirement 2 works as expected'() {

        given:
        URL resource = Resources.getResource(expectedOutcome);
        String expected = Resources.toString(resource, Charsets.UTF_8);

        String url = "http://localhost:$apiPort/search/titles"


        String completeUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", "genre:horror")
                .queryParam("full-fetch", fullFetch)
                .toUriString()

        when:
        String results = restTemplate.exchange(
                completeUrl,
                HttpMethod.GET,
                null,
                String).body

        then:
        results
        JSONAssert.assertEquals(expected, results, true)

        where:
        fullFetch | expectedOutcome
        "false"   | "requirement2_result_no_full_fetch.json"
        "true"    | "requirement2_result_full_fetch.json"

    }

    def 'requirement 2 validation level works as expected'() {
        given:
        String url = "http://localhost:$apiPort/search/titles"


        String completeUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", "genre:foo")
                .queryParam("full-fetch", "false")
                .toUriString()

        when:
        restTemplate.exchange(
                completeUrl,
                HttpMethod.GET,
                null,
                String).body

        then:
        HttpClientErrorException error = thrown()

        JSONAssert.assertEquals(
                "{\"timestamp\":\"2018-11-11T10:54:08.323+0000\",\"message\":\"not valid genre\",\"details\":\"uri=/search/titles\"}",

                error.responseBodyAsString,

                new CustomComparator(
                        JSONCompareMode.STRICT,
                        new Customization("timestamp", { t1, t2 -> true })
                )
        )

    }

}