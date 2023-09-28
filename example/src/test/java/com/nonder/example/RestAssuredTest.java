package com.nonder.example;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestAssuredTest {

    @LocalServerPort
    private int port;

    @Test
    public void testMovieDetails() {
        String baseURL = "http://localhost:" + port;
        int movieId = 12345;

        RestAssured.given()
                .baseUri(baseURL)
                .header("Accept", "application/json")
                .pathParam("id", movieId)
                .queryParam("language", "en-US")
                .when()
                .get("/movies/{id}")
                .then()
                .assertThat()
                .statusCode(200)
                .body("title", Matchers.equalTo("Inception"))
                .body("director", Matchers.equalTo("Christopher Nolan"));
    }
}
