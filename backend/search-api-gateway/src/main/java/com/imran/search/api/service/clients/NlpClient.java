package com.imran.search.api.service.clients;

import com.imran.search.api.dto.TextDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NlpClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.nlp}")
    private String nlpUrl;

    public NlpClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public double[] embed(String text) {
        return webClientBuilder.build()
                .post()
                .uri(nlpUrl)
                .bodyValue(new TextDTO(text))
                .retrieve()
                .bodyToMono(double[].class)
                .block();
    }
}
