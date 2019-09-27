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
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(
        loader = SpringBootContextLoader.class,
        classes = MoviesSearchServiceApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class Requirement4_ITSpec extends Specification {

    @LocalServerPort
    protected Integer apiPort

    protected RestTemplate restTemplate;

    def setup() {
        restTemplate = new RestTemplate();
    }

    /*
        Scenario Description:

        Find the coincidence: Given a query by the user, where the input is two actors/actresses names,
        the application replies with a list of movies or TV shows that both people have shared.

     */

    @Unroll("names = #names, expectedOutcome = #expectedOutcome")
    def 'requirement 4 works as expected'() {

        given:
        URL resource = Resources.getResource(expectedOutcome);
        String expected = Resources.toString(resource, Charsets.UTF_8);

        String url = "http://localhost:$apiPort/search/names/coincidence"

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json")
        HttpEntity<String> httpEntity = new HttpEntity<>(names, headers);


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
        names                                                                                         | expectedOutcome
        "{ \"names\": [\"Angelina Jolie\", \"Brad Pitt\"] }"                                          | "requirement4_Angelina_Jolie_Brad_Pitt_result.json"
        "{ \"names\":[\"Orlando Bloom\", \"Johnny Depp\"] }"                                          | "requirement4_Orlando_Bloom_Johnny_Depp_result.json"
        "{ \"names\":[\"Brad Pitt\", \"Robert Redford\"] }"                                           | "requirement4_Brad_Pitt_Robert_Redford_result.json"
        "{ \"names\":[\"George Clooney\", \"Brad Pitt\"] }"                                           | "requirement4_George_Clooney_Brad_Pitt_result.json"
        "{ \"names\":[\"George Clooney\", \"Matt Damon\"] }"                                          | "requirement4_George_Clooney_Matt_Damon_result.json"
        "{ \"names\":[\"George Clooney\", \"Matt Damon\", \"Brad Pitt\"] }"                           | "requirement4_George_Clooney_Matt_Damon_Brad_Pitt_result.json"
        "{ \"names\":[\"John Travolta\", \"Uma Thurman\", \"Samuel L. Jackson\", \"Bruce Willis\"] }" | "requirement4_PulpFiction.json"
        "{ \"names\":[\"Al Pacino\", \"Robert De Niro\", \"Val Kilmer\", \"Jon Voight\"] }"           | "requirement4_Heat.json"
    }

    @Unroll("names = #names, errorMessage = #errorMessage")
    def 'requirement 4 validation level works as expected'() {

        given:
        String url = "http://localhost:$apiPort/search/names/coincidence"

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json")
        HttpEntity<String> httpEntity = new HttpEntity<>(names, headers);


        when:
        restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
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
        names                                | errorMessage
        "{ \"names\": [] }"                  | "{\"timestamp\":\"2018-11-11T13:31:14.860+0000\",\"message\":\"please provide at least two full names\",\"details\":\"uri=/search/names/coincidence\"}"
        "{ \"names\":[\"George Clooney\"] }" | "{\"timestamp\":\"2018-11-11T13:31:14.860+0000\",\"message\":\"please provide at least two full names\",\"details\":\"uri=/search/names/coincidence\"}"
        "{ \"names\":null }" | "{\"timestamp\":\"2018-11-11T13:31:14.860+0000\",\"message\":\"please provide at least two full names\",\"details\":\"uri=/search/names/coincidence\"}"

        //TODO case here
        "{ \"names\":[\"George Clooney\"] }" | "{\"timestamp\":\"2018-11-11T13:31:14.860+0000\",\"message\":\"please provide at least two full names\",\"details\":\"uri=/search/names/coincidence\"}"

    }

}
