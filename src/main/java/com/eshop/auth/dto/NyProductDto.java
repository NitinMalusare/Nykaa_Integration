package com.eshop.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class NyProductDto {

    @NotBlank(message = "SKU is required")
    @JsonProperty("sku")
    private String sku;

    @JsonProperty("vendorSkuCode")
    private String vendorSkuCode;

    @JsonProperty("mfgSkuCode")
    private String mfgSkuCode;

    @NotBlank(message = "SKU name is required")
    @JsonProperty("skuName")
    private String skuName;

    @NotBlank(message = "MRP is required")
    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "Invalid MRP format")
    @JsonProperty("mrp")
    private String mrp;

    @NotBlank(message = "Sale price is required")
    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "Invalid sale price format")
    @JsonProperty("salePrice")
    private String salePrice;

    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "Invalid base cost format")
    @JsonProperty("baseCost")
    private String baseCost;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be either ACTIVE or INACTIVE")
    @JsonProperty("status")
    private String status;

    @JsonProperty("lastUpdateDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private String lastUpdateDate;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("color")
    private String color;

    @JsonProperty("size")
    private String size;

    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "Invalid weight format")
    @JsonProperty("weight")
    private String weight;

    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "Invalid length format")
    @JsonProperty("length")
    private String length;

    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "Invalid width format")
    @JsonProperty("width")
    private String width;

    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "Invalid height format")
    @JsonProperty("height")
    private String height;

    @Pattern(regexp = "^(Y|N)$", message = "Serial tracking must be either Y or N")
    @JsonProperty("serialTracking")
    private String serialTracking;

    @JsonProperty("sellerId")
    private String sellerId;
}
