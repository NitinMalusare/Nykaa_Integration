package com.eshop.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id")
    private Long orderId;
    
    @Column(name = "order_no", nullable = false)
    private String orderNo;
    
    @Column(name = "line_no")
    private String lineNo;
    
    @Column(name = "delivery_mode")
    private String deliveryMode;
    
    @Column(name = "sku_code")
    private String skuCode;
    
    @Column(name = "sku_name")
    private String skuName;
    
    @Column(name = "order_qty")
    private String orderQty;
    
    @Column(name = "rejected_qty")
    private String rejectedQty;
    
    @Column(name = "cancelled_qty")
    private String cancelledQty;
    
    @Column(name = "shipped_qty")
    private String shippedQty;
    
    @Column(name = "returned_qty")
    private String returnedQty;
    
    @Column(name = "delivered_qty")
    private String deliveredQty;
    
    @Column(name = "line_amount")
    private String lineAmount;
    
    @Column(name = "line_tax_amount")
    private String lineTaxAmount;
    
    @Column(name = "unit_price")
    private String unitPrice;
    
    @Column(name = "mrp")
    private String mrp;
    
    @Column(name = "discount_amount")
    private String discountAmount;
    
    @Column(name = "shipping_charge")
    private String shippingCharge;
    
    @Column(name = "cod_charge")
    private String codCharge;
    
    @Column(name = "invoice_no")
    private String invoiceNo;
    
    @Column(name = "trans_code")
    private String transCode;
    
    @Column(name = "trans_name")
    private String transName;
    
    @Column(name = "awb_no")
    private String awbNo;
    
    @Column(name = "imei_nos")
    private String imeiNos;
    
    @Column(name = "confirm_date")
    private LocalDateTime confirmDate;
    
    @Column(name = "gv_amount")
    private String gvAmount;
    
    @Column(name = "store_credit")
    private String storeCredit;
    
    @Column(name = "line_status")
    private String lineStatus;
    
    @Column(name = "cst")
    private String cst;
    
    @Column(name = "vat")
    private String vat;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

