package com.eshop.auth.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderDetailDTO {
    private String orderNo;
    private String orderStatus;
    private String orderDate;
    private String sla;
    private String customerName;
    private String paymentMethod;
    private String orderAmount;
    private String orderCurrency;
    private String orderTaxAmount;
    private String onHold;
    private String shippingCharge;
    private String discountAmount;
    private String discCouponCode;
    private String storeCredit;
    private String gvAmount;
    private String codCharge;
    private String address1;
    private String address2;
    private String address3;
    private String phone;
    private String email;
    private String countryCode;
    private String state;
    private String city;
    private String pinCode;
    private String billToName;
    private String billAddress1;
    private String billAddress2;
    private String billAddress3;
    private String billPhone;
    private String billEmail;
    private String billCountryCode;
    private String billState;
    private String billCity;
    private String billZipCode;
    private String cancelRemark;
    private String reasonForCancellation;
    private String priority;
    private String orderLastUpdateDate;
    private String masterOrderNo;
    private String gstin;
    private String sellerId;
    private List<OrderItemDTO> orderItems;
}

