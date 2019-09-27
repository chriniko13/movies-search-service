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

class Requirement3_ITSpec extends Specification {

    @LocalServerPort
    protected Integer apiPort

    protected RestTemplate restTemplate;

    def setup() {
        restTemplate = new RestTemplate();
    }

    /*
        Scenario Description:

        Typecasting: Given a query by the user, where he/she provides an actor/actress name,
        the system should determine if that person has become typecasted (at least half of their work is one genre).

     */

    @Unroll("actorName = #actorName, expectedOutcome = #expectedOutcome")
    def 'requirement 3 works as expected'() {

        given:
        URL resource = Resources.getResource(expectedOutcome);
        String expected = Resources.toString(resource, Charsets.UTF_8);

        String url = "http://localhost:$apiPort/search/names"


        String completeUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("name", actorName)
                .queryParam("full-fetch", "false")
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
        actorName         | expectedOutcome
        "Will Smith"      | "requirement3_Will_Smith_result.json"
        "Charles Bronson" | "requirement3_Charles_Bronson_result.json"
        "Steve McQueen"   | "requirement3_Steve_McQueen_result.json"
        "Danny DeVito"    | "requirement3_Danny_DeVito_result.json"
    }

    @Unroll("name = #name, errorMessage = #errorMessage")
    def 'requirement 3 validation level works as expected'() {
        given:
        String url = "http://localhost:$apiPort/search/names"


        String completeUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("name", name)
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
                errorMessage,

                error.responseBodyAsString,

                new CustomComparator(
                        JSONCompareMode.STRICT,
                        new Customization("timestamp", { t1, t2 -> true })
                )
        )

        where:
        name      | errorMessage
        "JohnDoe" | "{\"timestamp\":\"2018-11-11T11:14:24.253+0000\",\"message\":\"name should have the following format, eg: John Doe, and should be at least 8 characters.\",\"details\":\"uri=/search/names\"}"
        "Foo Bar" | "{\"timestamp\":\"2018-11-11T11:14:24.253+0000\",\"message\":\"name should have the following format, eg: John Doe, and should be at least 8 characters.\",\"details\":\"uri=/search/names\"}"
    }
}
