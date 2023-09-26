package com.nonder.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestAssuredTest {

    @LocalServerPort
    private int port;

    @Test
    public void testMovieDetails() {
        String baseURL = "http://localhost:" + port;
        int movieId = 12345;

        given()
                .baseUri(baseURL)
                .header("Accept", "application/json")
                .pathParam("id", movieId)
                .queryParam("language", "en-US")
                .when()
                .get("/movies/{id}")
                .then()
                .assertThat()
                .statusCode(200)
                .body("title", equalTo("Inception"))
                .body("director", equalTo("Christopher Nolan"));
    }
}
