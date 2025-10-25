package com.eshop.auth.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderListResponseDTO {
    
    private Integer responseCode;
    private String responseMessage;
    private List<OrderDTO> orderList;
}
