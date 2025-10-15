package com.eshop.auth.dto;

import lombok.Data;
import java.util.List;

@Data
public class NyProductRequestDto {
    private String updatedDate;
    private List<String> skuCode;
    private Integer pageNumber;
    private Integer limit;
}
