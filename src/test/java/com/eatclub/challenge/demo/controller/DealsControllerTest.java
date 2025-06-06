package com.eatclub.challenge.demo.controller;

import com.eatclub.challenge.demo.dto.DealResponseDTO;
import com.eatclub.challenge.demo.model.Restaurant;
import com.eatclub.challenge.demo.service.DealsService;
import com.eatclub.challenge.demo.service.RestaurantService;
import com.eatclub.challenge.demo.wrapper.RestaurantWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealsControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DealsService dealsService;

    private List<Restaurant> mockRestaurants;

    @BeforeEach
    void setUp() throws Exception {
        InputStream mockStream = getClass().getResourceAsStream("/deals_response.json");
        ObjectMapper objectMapper = new ObjectMapper();
        RestaurantWrapper wrapper = objectMapper.readValue(mockStream, RestaurantWrapper.class);
        mockRestaurants = wrapper.getRestaurants();
    }

    @Test
    void testGetDeals_fromJsonFile() throws Exception {
        when(restaurantService.getAllRestaurants()).thenReturn(mockRestaurants);

        List<List<DealResponseDTO>> result = dealsService.getDealsByTime("3:00pm");

        assertEquals(1, result.size());
        assertEquals("3:00pm", result.get(0).get(0).getRestaurantOpen());
        assertEquals("3:00pm", result.get(0).get(1).getRestaurantOpen());
    }

    @Test
    void testGetAllRestaurants_RestClientException() {
        RestaurantService realService = new RestaurantService(restTemplate);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenThrow(new RestClientException("Connection timeout"));

        assertThrows(RestClientException.class, realService::getAllRestaurants);
    }

    @Test
    void testGetAllRestaurants_ServiceUnavailable() {
        RestaurantService realService = new RestaurantService(restTemplate);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenThrow(HttpServerErrorException.create(
                        HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", HttpHeaders.EMPTY, null, null));

        HttpServerErrorException exception = assertThrows(HttpServerErrorException.class, realService::getAllRestaurants);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    }

    @Test
    void shouldReturnStatusFromHttpStatusCodeException() {
        RestaurantService realService = new RestaurantService(restTemplate);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found"));

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, realService::getAllRestaurants);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
