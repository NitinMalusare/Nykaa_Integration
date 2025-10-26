package com.eshop.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_no", nullable = false, unique = true)
    private String orderNo;
    
    @Column(name = "order_status", nullable = false)
    private String orderStatus;
    
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;
    
    @Column(name = "order_last_update_date", nullable = false)
    private LocalDateTime orderLastUpdateDate;
    
    @Column(name = "on_hold", nullable = false)
    private String onHold;
    
    @Column(name = "seller_id", nullable = false)
    private String sellerId;
    
    @Column(name = "seller_type")
    private String sellerType; // DROPSHIP or JIT
    
    // Additional detailed fields
    @Column(name = "sla")
    private String sla;
    
    @Column(name = "customer_name")
    private String customerName;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "order_amount")
    private String orderAmount;
    
    @Column(name = "order_currency")
    private String orderCurrency;
    
    @Column(name = "order_tax_amount")
    private String orderTaxAmount;
    
    @Column(name = "shipping_charge")
    private String shippingCharge;
    
    @Column(name = "discount_amount")
    private String discountAmount;
    
    @Column(name = "disc_coupon_code")
    private String discCouponCode;
    
    @Column(name = "store_credit")
    private String storeCredit;
    
    @Column(name = "gv_amount")
    private String gvAmount;
    
    @Column(name = "cod_charge")
    private String codCharge;
    
    @Column(name = "address1")
    private String address1;
    
    @Column(name = "address2")
    private String address2;
    
    @Column(name = "address3")
    private String address3;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "country_code")
    private String countryCode;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "pin_code")
    private String pinCode;
    
    @Column(name = "bill_to_name")
    private String billToName;
    
    @Column(name = "bill_address1")
    private String billAddress1;
    
    @Column(name = "bill_address2")
    private String billAddress2;
    
    @Column(name = "bill_address3")
    private String billAddress3;
    
    @Column(name = "bill_phone")
    private String billPhone;
    
    @Column(name = "bill_email")
    private String billEmail;
    
    @Column(name = "bill_country_code")
    private String billCountryCode;
    
    @Column(name = "bill_state")
    private String billState;
    
    @Column(name = "bill_city")
    private String billCity;
    
    @Column(name = "bill_zip_code")
    private String billZipCode;
    
    @Column(name = "cancel_remark")
    private String cancelRemark;
    
    @Column(name = "reason_for_cancellation")
    private String reasonForCancellation;
    
    @Column(name = "priority")
    private String priority;
    
    @Column(name = "master_order_no")
    private String masterOrderNo;
    
    @Column(name = "gstin")
    private String gstin;
    
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
