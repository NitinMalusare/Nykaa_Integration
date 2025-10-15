package com.eshop.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class NyProductResponseDto {
    @NotNull
    @JsonProperty("responseCode")
    private Integer responseCode;

    @NotNull
    @JsonProperty("responseMessage")
    private String responseMessage;

    @JsonProperty("productList")
    private List<NyProductDto> productList;

    @NotNull
    @JsonProperty("token")
    private String token;
}
