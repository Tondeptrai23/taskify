package com.taskify.apigateway.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class IamClient {
    private final WebClient webClient;

    public IamClient(WebClient.Builder webClientBuilder,
                      @Value("${services.iam.url}") String iamServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(iamServiceUrl).build();
    }

    public WebClient getWebClient() {
        return webClient;
    }
}
