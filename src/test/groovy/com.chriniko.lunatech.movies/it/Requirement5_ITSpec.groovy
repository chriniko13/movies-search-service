package com.chriniko.lunatech.movies.it

import com.chriniko.lunatech.movies.MoviesSearchServiceApplication
import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(
        loader = SpringBootContextLoader.class,
        classes = MoviesSearchServiceApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@Ignore("it not always pass due to different dataset loaded to Neo4J db.")
class Requirement5_ITSpec extends Specification {

    @LocalServerPort
    protected Integer apiPort

    protected RestTemplate restTemplate;

    def setup() {
        restTemplate = new RestTemplate();
    }

    /*
        Scenario Description:

        Six degrees of Kevin Bacon: Given a query by the user, you must provide what’s the degree of
        separation between the person (e.g. actor or actress) the user has entered and Kevin Bacon.


     */

    @Unroll("links = #links, expectedOutcome = #expectedOutcome")
    def 'requirement 5 works as expected'() {

        given:
        URL resource = Resources.getResource(expectedOutcome);
        String expected = Resources.toString(resource, Charsets.UTF_8);

        String url = "http://localhost:$apiPort/acquaintance-links"

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json")
        HttpEntity<String> httpEntity = new HttpEntity<>(links, headers);


        when:
        String results = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                String).body

        then:
        results
        JSONAssert.assertEquals(expected, results, true)

        where:
        links                                                                     | expectedOutcome
        "{\"sourceFullName\":\"Matt Damon\", \"targetFullName\":\"Henry Fonda\"}" | "requirement5_MattDamon_HenryFonda.json"
        "{\"sourceFullName\":\"Matt Damon\", \"targetFullName\":\"Kevin Bacon\"}" | "requirement5_MattDamon_KevinBacon.json"
        "{\"sourceFullName\":\"Matt Damon\"}"                                     | "requirement5_MattDamon_KevinBacon.json"
        "{\"sourceFullName\":\"Matt Damon\", \"targetFullName\":\"Matt Damon\"}"  | "requirement5_MattDamon_MattDamon.json"

    }


    def 'requirement 5 works as expected -- target name not exists'() {
        given:

        String url = "http://localhost:$apiPort/acquaintance-links"

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json")

        HttpEntity<String> httpEntity = new HttpEntity<>(
                "{\"sourceFullName\":\"Matt Damon\", \"targetFullName\":\"Fooo Barr\"}",
                headers
        )


        when:
        String results = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                String).body

        then:

        JSONAssert.assertEquals(
                "{\"providedSourceFullName\":\"Matt Damon\",\"providedTargetName\":\"Fooo Barr\",\"results\":[]}",
                results,
                true
        )

    }

    def 'requirement 5 works as expected -- source name node not exists'() {
        given:

        String url = "http://localhost:$apiPort/acquaintance-links"

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json")

        HttpEntity<String> httpEntity = new HttpEntity<>(
                "{\"sourceFullName\":\"Matt Damon\", \"targetFullName\":\"Fooo Barr\"}",
                headers
        )


        when:
        String results = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                String).body

        then:

        JSONAssert.assertEquals(
                "{\"providedSourceFullName\":\"Matt Damon\",\"providedTargetName\":\"Fooo Barr\",\"results\":[]}",
                results,
                true
        )

    }

    def 'requirement 5 works as expected -- source and name node not exist'() {
        given:

        String url = "http://localhost:$apiPort/acquaintance-links"

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json")

        HttpEntity<String> httpEntity = new HttpEntity<>(
                "{\"sourceFullName\":\"Fooo Barr1\", \"targetFullName\":\"Fooo Barr2\"}",
                headers
        )


        when:
        String results = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                String).body

        then:

        JSONAssert.assertEquals(
                "{\"providedSourceFullName\":\"Fooo Barr1\",\"providedTargetName\":\"Fooo Barr2\",\"results\":[]}",
                results,
                true
        )
    }

}
