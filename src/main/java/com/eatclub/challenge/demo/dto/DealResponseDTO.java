package com.eatclub.challenge.demo.dto;

import lombok.Data;

@Data
public class DealResponseDTO {
        private String restaurantObjectId;
        private String restaurantName;
        private String restaurantAddress1;
        private String restarantSuburb;
        private String restaurantOpen;
        private String restaurantClose;

        private String dealObjectId;
        private String discount;
        private String dineIn;
        private String lightning;
        private String qtyLeft;


}