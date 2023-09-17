package com.nonder.ecomflowtesting.client;

import com.nonder.ecomflowtesting.dto.InventoryResponse;
import com.nonder.ecomflowtesting.model.Order;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class InventoryServiceClient {

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    private WebClient webClient;
    private final WebClient.Builder webClientBuilder;

    public InventoryServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder.baseUrl(inventoryServiceUrl).build();
    }

    public boolean checkInventory(Order order) {
        InventoryResponse response = webClient.post()
                .uri("/api/inventory/check")
                .bodyValue(order)
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .block();

        if(response != null && response.isAvailable()) {
            return true;
        }
        return false;
    }
}
