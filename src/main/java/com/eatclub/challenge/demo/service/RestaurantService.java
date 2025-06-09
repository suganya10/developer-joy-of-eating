package com.eatclub.challenge.demo.service;

import com.eatclub.challenge.demo.model.Restaurant;
import com.eatclub.challenge.demo.wrapper.RestaurantWrapper;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class RestaurantService {

    @Setter
    @Value("${deals.url}")
    private String dealsUrl;

    private static final Logger log = LoggerFactory.getLogger(RestaurantService.class);
    private final RestTemplate restTemplate;

    public RestaurantService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Restaurant> getAllRestaurants() {
        try {
            var responseEntity = restTemplate.exchange(
                    dealsUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestaurantWrapper>() {}
            );

            return (responseEntity != null && responseEntity.getBody() != null)
                    ? responseEntity.getBody().getRestaurants()
                    : Collections.emptyList();

        } catch (HttpStatusCodeException e) {
            log.error("HTTP error while fetching restaurant details. Status code: {}, Response body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e;

        } catch (RestClientException e) {
            log.error("Failed to fetch restaurant details from API: {}", e.getMessage(), e);
            throw e;
        }
    }
}
