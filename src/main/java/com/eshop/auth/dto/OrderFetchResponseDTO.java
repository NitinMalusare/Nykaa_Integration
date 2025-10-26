package com.eshop.auth.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderFetchResponseDTO {
    private int responseCode;
    private String responseMessage;
    private List<OrderDetailDTO> orders;
}

