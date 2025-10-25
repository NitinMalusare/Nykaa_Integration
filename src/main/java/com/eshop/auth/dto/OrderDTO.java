package com.eshop.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderDTO {
    
    @JsonProperty("orderNo")
    private String orderNo;
    
    @JsonProperty("orderStatus")
    private String orderStatus;
    
    @JsonProperty("orderDate")
    private String orderDate;
    
    @JsonProperty("orderLastUpdateDate")
    private String orderLastUpdateDate;
    
    @JsonProperty("onHold")
    private String onHold;
    
    @JsonProperty("sellerId")
    private String sellerId;
}
