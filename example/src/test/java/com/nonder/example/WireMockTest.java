package com.nonder.example;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
public class WireMockTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        // Start wiremock server
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8081));
        wireMockServer.start();

        // Setup stubs
        wireMockServer.stubFor(WireMock.get("/user/123")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": \"123\", \"name\": \"John Doe\", \"email\": \"john.doe@example.com\"}")));
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void happyFlowTest() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .build();

        WebClient.ResponseSpec responseSpec = webClient.get()
                .uri("/user/123")
                .retrieve();

        ResponseEntity<String> responseEntity = responseSpec.toEntity(String.class).block();
        String responseBody = responseEntity.getBody();
        HttpStatusCode responseCode = responseEntity.getStatusCode();

        Assert.assertEquals(HttpStatus.OK, responseCode);

        Assert.assertNotNull(responseBody);
        Assert.assertEquals("{\"id\": \"123\", \"name\": \"John Doe\", \"email\": \"john.doe@example.com\"}", responseBody);
    }
}