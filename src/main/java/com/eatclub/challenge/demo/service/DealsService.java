package com.eatclub.challenge.demo.service;

import com.eatclub.challenge.demo.dto.DealResponseDTO;
import com.eatclub.challenge.demo.model.Restaurant;
import com.eatclub.challenge.demo.utility.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.eatclub.challenge.demo.utility.MapperUtil.getMappedDeals;

@Service
public class DealsService {

    private static final Logger log = LoggerFactory.getLogger(DealsService.class);
    private final RestaurantService restaurantService;

    public DealsService(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    public List<List<DealResponseDTO>> getDealsByTime(String timeOfDay) throws IOException {
        log.info("Fetching restaurant data for time: {}", timeOfDay);

        List<Restaurant> restaurants;
        try {
            restaurants = restaurantService.getAllRestaurants();
        } catch (Exception e) {
            log.error("Error fetching restaurant data: {}", e.getMessage(), e);
            throw new IOException("Unable to fetch restaurant data", e);
        }

        List<List<DealResponseDTO>> dealResponses = new ArrayList<>();

        for (Restaurant res : restaurants) {
            if (timeOfDay.equalsIgnoreCase(res.getOpen())) {
                dealResponses.add(getMappedDeals(res));
            }
        }

        if (dealResponses.isEmpty()) {
            log.warn("No deals found for time: {}", timeOfDay);
        }

        return dealResponses;
    }

    public Map<String, String> getDealsPeakTime() throws IOException {
        List<Restaurant> restaurants;

        try {
            restaurants = restaurantService.getAllRestaurants();
        } catch (Exception e) {
            log.error("Error fetching restaurant data: {}", e.getMessage(), e);
            throw new IOException("Unable to fetch restaurant data", e);
        }

        Map<String, Integer> peakStart = new HashMap<>();
        Map<String, Integer> peakEnd = new HashMap<>();

        for (Restaurant res : restaurants) {
            String open = res.getOpen();
            String close = res.getClose();

            if (open != null && !open.isEmpty()) {
                peakStart.put(open, peakStart.getOrDefault(open, 0) + 1);
            }

            if (close != null && !close.isEmpty()) {
                peakEnd.put(close, peakEnd.getOrDefault(close, 0) + 1);
            }
        }

        String peakTimeStart = peakStart.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No Peak StartTime");

        String peakTimeEnd = peakEnd.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No Peak EndTime");


        Map<String, String> result = new LinkedHashMap<>();
        result.put("peakTimeStart", peakTimeStart);
        result.put("peakTimeEnd", peakTimeEnd);
        return result;
    }

}
