package com.eatclub.challenge.demo.utility;

import com.eatclub.challenge.demo.model.Deal;
import com.eatclub.challenge.demo.dto.DealResponseDTO;
import com.eatclub.challenge.demo.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class MapperUtil {
    public static List<DealResponseDTO> getMappedDeals(Restaurant restaurant) {
        List<DealResponseDTO> responseList = new ArrayList<>();

            for (Deal deal : restaurant.getDeals()) {
                DealResponseDTO response = new DealResponseDTO();
                response.setRestaurantObjectId(restaurant.getObjectId());
                response.setRestaurantName(restaurant.getName());
                response.setRestaurantAddress1(restaurant.getAddress1());
                response.setRestarantSuburb(restaurant.getSuburb());
                response.setRestaurantOpen(restaurant.getOpen());
                response.setRestaurantClose(restaurant.getClose());

                response.setDealObjectId(deal.getObjectId());
                response.setDiscount(deal.getDiscount());
                response.setDineIn(deal.getDineIn());
                response.setLightning(deal.getLightning());
                response.setQtyLeft(deal.getQtyLeft());

                responseList.add(response);
            }

        return responseList;
    }


}
