package com.starwars.challenge.conexa.client;

import com.starwars.challenge.conexa.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.Duration;

@Component
public class SwapiClientImpl implements SwapiClient{

    private final WebClient webClient;

    public SwapiClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public <T> T getPaged(String path, String searchParamName, String searchParamValue, int page, int limit, Class<T> responseType) {
        return webClient.get()
                .uri(uriBuilder -> buildPagedUri(uriBuilder, path, searchParamName, searchParamValue, page, limit))
                .retrieve()
                .bodyToMono(responseType)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    @Override
    public <T> T get(String path, Class<T> responseType) {
        return webClient.get()
                .uri(path)
                .retrieve()
                .bodyToMono(responseType)
                .onErrorMap(WebClientResponseException.NotFound.class,
                        ex -> new ResourceNotFoundException("Resource not found in SWAPI: " + path))
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    private URI buildPagedUri(UriBuilder uriBuilder, String path, String searchParamName, String searchParamValue, int page, int limit) {
        uriBuilder.path(path)
                .queryParam("page", page)
                .queryParam("limit", limit)
                .queryParam("expanded", true);

        if (hasValue(searchParamValue)) {
            uriBuilder.queryParam(searchParamName, searchParamValue);
        }
        return uriBuilder.build();
    }

    private boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }

}
