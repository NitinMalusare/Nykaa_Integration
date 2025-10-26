package com.eshop.auth.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private String lineNo;
    private String deliveryMode;
    private String skuCode;
    private String skuName;
    private String orderQty;
    private String rejectedQty;
    private String cancelledQty;
    private String shippedQty;
    private String returnedQty;
    private String deliveredQty;
    private String lineAmount;
    private String lineTaxAmount;
    private String unitPrice;
    private String mrp;
    private String discountAmount;
    private String shippingCharge;
    private String codCharge;
    private String invoiceNo;
    private String transCode;
    private String transName;
    private String awbNo;
    private String imeiNos;
    private String confirmDate;
    private String gvAmount;
    private String storeCredit;
    private String lineStatus;
    private String cst;
    private String vat;
}

