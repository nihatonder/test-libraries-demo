package com.nonder.ecomflowtesting.client;

import com.nonder.ecomflowtesting.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class InventoryServiceClient {

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    private final WebClient webClient;

    public InventoryServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(inventoryServiceUrl).build();
    }

    public boolean checkInventory(Order order) {
        Boolean isAvailable = webClient.post()
                .uri("/api/inventory/check")
                .bodyValue(order)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        return isAvailable != null && isAvailable;
    }
}
