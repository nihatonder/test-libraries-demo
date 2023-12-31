package com.nonder.ecomflowtesting.client;

import com.nonder.ecomflowtesting.dto.InventoryResponse;
import com.nonder.ecomflowtesting.model.Order;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class InventoryServiceClient {

    private final WebClient.Builder webClientBuilder;
    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;
    private WebClient webClient;

    public InventoryServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder.baseUrl(inventoryServiceUrl).build();
    }

    public ResponseEntity checkInventory(@RequestParam int id, @RequestParam int quantity) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/inventory/check")
                            .queryParam("id", id)
                            .queryParam("quantity", quantity)
                            .build())
                    .retrieve()
                    .toEntity(InventoryResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception exc) {
            return ResponseEntity.status(500).body(exc.getMessage());
        }
    }
}
