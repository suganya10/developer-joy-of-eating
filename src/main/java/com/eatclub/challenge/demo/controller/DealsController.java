package com.eatclub.challenge.demo.controller;

import com.eatclub.challenge.demo.dto.DealResponseDTO;
import com.eatclub.challenge.demo.service.DealsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DealsController {

    private static final Logger log = LoggerFactory.getLogger(DealsController.class);
    private final DealsService dealsService;

    public DealsController(DealsService dealsService) {
        this.dealsService = dealsService;
    }

    /**
     * Retrieves deals available at a specific time of day.
     *
     * @param timeOfDay the time of day to filter deals (e.g., "3:00pm")
     * @return a ResponseEntity containing a map of deal responses
     */
    @GetMapping("/deals/{timeOfDay}")
    public ResponseEntity<Map<String, List<DealResponseDTO>>> getDealsByTime(@PathVariable String timeOfDay) {
        try {
            List<DealResponseDTO> groupedDeals = dealsService.getDealsByTime(timeOfDay);

            return ResponseEntity.ok(Map.of("deals", groupedDeals));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    /**
     * Retrieves the peak time window during which the most deals are available.
     *
     * @return a ResponseEntity containing a map with the peak start and end times
     */
    @GetMapping("/deals/peakTime")
    public ResponseEntity<Map<String, String>> getPeakTimeDeals() {
        try {
            Map<String, String> response = dealsService.getDealsPeakTime();
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Failed to retrieve deals: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
