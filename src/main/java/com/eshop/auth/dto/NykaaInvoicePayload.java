package com.eshop.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO representing the incoming payload for Nykaa invoice rendering.
 */
@Data
public class NykaaInvoicePayload {

    @JsonProperty("header_info")
    private HeaderInfo headerInfo;

    @JsonProperty("seller_details")
    private SellerDetails sellerDetails;

    @JsonProperty("buyer_details")
    private BuyerDetails buyerDetails;

    @JsonProperty("product_items")
    private List<ProductItem> productItems;

    @JsonProperty("financial_summary")
    private FinancialSummary financialSummary;

    @Data
    public static class HeaderInfo {
        @JsonProperty("order_number")
        private String orderNumber;

        @JsonProperty("nykaa_order_no")
        private String nykaaOrderNo;

        @JsonProperty("order_date")
        private String orderDate;

        @JsonProperty("transporter")
        private String transporter;

        @JsonProperty("invoice_number")
        private String invoiceNumber;

        @JsonProperty("invoice_date")
        private String invoiceDate;

        @JsonProperty("payment_mode")
        private String paymentMode;

        @JsonProperty("billing_state")
        private String billingState;

        @JsonProperty("place_of_supply")
        private String placeOfSupply;

        @JsonProperty("awb_no")
        private String awbNo;

        @JsonProperty("items_in_the_box")
        private Integer itemsInTheBox;
    }

    @Data
    public static class SellerDetails {
        @JsonProperty("pincode")
        private String pincode;

        @JsonProperty("name")
        private String name;

        @JsonProperty("gstin")
        private String gstin;

        @JsonProperty("address_line_1")
        private String addressLine1;

        @JsonProperty("address_line_2")
        private String addressLine2;
    }

    @Data
    public static class BuyerDetails {
        @JsonProperty("name")
        private String name;

        @JsonProperty("address_line_1")
        private String addressLine1;

        @JsonProperty("city")
        private String city;

        @JsonProperty("state_pincode")
        private String statePincode;

        @JsonProperty("country")
        private String country;

        @JsonProperty("buyer_uid_gstin")
        private String buyerUidGstin;
    }

    @Data
    public static class ProductItem {
        @JsonProperty("s_no")
        private Integer serialNo;

        @JsonProperty("product_name")
        private String productName;

        @JsonProperty("description")
        private String description;

        @JsonProperty("hsn")
        private String hsn;

        @JsonProperty("qty")
        private Integer qty;

        @JsonProperty("unit_price")
        private Double unitPrice;

        @JsonProperty("discount")
        private Double discount;

        @JsonProperty("taxable_value")
        private Double taxableValue;

        @JsonProperty("cgst")
        private Double cgst;

        @JsonProperty("sgst_utgst")
        private Double sgstUtgst;

        @JsonProperty("igst")
        private Double igst;

        @JsonProperty("total")
        private Double total;
    }

    @Data
    public static class FinancialSummary {
        @JsonProperty("total_amount_taxable")
        private Double totalAmountTaxable;

        @JsonProperty("total_tax_igst")
        private Double totalTaxIgst;

        @JsonProperty("net_payable")
        private Double netPayable;
    }
}


