package com.eatclub.challenge.demo.service;

import com.eatclub.challenge.demo.dto.DealResponseDTO;
import com.eatclub.challenge.demo.model.Deal;
import com.eatclub.challenge.demo.model.Restaurant;
import com.eatclub.challenge.demo.utility.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/*

 */

@Service
public class DealsService {

    private static final Logger log = LoggerFactory.getLogger(DealsService.class);
    private final RestaurantService restaurantService;

    public DealsService(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    public List<DealResponseDTO> getDealsByTime(String timeOfDay) throws IOException {
        log.info("Fetching restaurant data for time: {}", timeOfDay);

        List<Restaurant> restaurants;
        try {
            restaurants = restaurantService.getAllRestaurants();
        } catch (Exception e) {
            log.error("Error fetching restaurant data: {}", e.getMessage(), e);
            throw new IOException("Unable to fetch restaurant data", e);
        }

        List<DealResponseDTO> dealResponses = restaurants.stream()
                .filter(res -> res.getDeals().stream()
                        .anyMatch(deal -> timeOfDay.equalsIgnoreCase(deal.getStart())))
                .flatMap(res -> MapperUtil.getMappedDeals(res).stream())
                .collect(Collectors.toList());

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

            List<Deal> deals = res.getDeals();
            if (deals != null) {
                for (Deal deal : deals) {
                    String start = deal.getStart();
                    String end = deal.getEnd();

                    if (start != null && !start.isEmpty()) {
                        peakStart.put(start, peakStart.getOrDefault(start, 0) + 1);
                    }

                    if (end != null && !end.isEmpty()) {
                        peakEnd.put(end, peakEnd.getOrDefault(end, 0) + 1);
                    }
                }
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
