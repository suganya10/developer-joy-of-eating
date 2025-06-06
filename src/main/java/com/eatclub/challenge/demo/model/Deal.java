package com.eatclub.challenge.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Deal {
    private String objectId;
    private String discount;
    private String dineIn;
    private String lightning;
    private String start;
    private String end;
    private String qtyLeft;

}
