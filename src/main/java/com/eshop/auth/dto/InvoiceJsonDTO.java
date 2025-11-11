package com.eshop.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceJsonDTO {

    @JsonProperty("invoiceInfo")
    private InvoiceInfoDTO invoiceInfo;

    @JsonProperty("sellerDetails")
    private SellerInfoDTO sellerDetails;

    @JsonProperty("buyerDetails")
    private BuyerInfoDTO buyerDetails;

    @JsonProperty("orderItems")
    private List<InvoiceItemDTO> orderItems;

    @JsonProperty("financialSummary")
    private FinancialSummaryDTO financialSummary;

    @Data
    public static class InvoiceInfoDTO {
        @JsonProperty("invoiceNo")
        private String invoiceNo;

        @JsonProperty("invoiceDate")
        private String invoiceDate;

        @JsonProperty("orderNo")
        private String orderNo;

        @JsonProperty("orderDate")
        private String orderDate;
    }

    @Data
    public static class SellerInfoDTO {
        @JsonProperty("name")
        private String name;

        @JsonProperty("address")
        private String address;

        @JsonProperty("gstin")
        private String gstin;
    }

    @Data
    public static class BuyerInfoDTO {
        @JsonProperty("name")
        private String name;

        @JsonProperty("shippingAddress")
        private String shippingAddress;

        @JsonProperty("billingAddress")
        private String billingAddress;
    }

    @Data
    public static class FinancialSummaryDTO {
        @JsonProperty("totalAmount")
        private double totalAmount;

        @JsonProperty("totalTax")
        private double totalTax;

        @JsonProperty("netPayable")
        private double netPayable;
    }
}
