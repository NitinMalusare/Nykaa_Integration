package com.eshop.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ny_products", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"sku", "token"})
       })
public class NyProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "SKU is required")
    @Column(nullable = false)
    private String sku;

    @NotBlank(message = "Token is required")
    @Column(nullable = false)
    private String token;
    
    private String vendorSkuCode;
    private String mfgSkuCode;

    @NotBlank(message = "SKU name is required")
    @Column(nullable = false)
    private String skuName;

    @NotBlank(message = "MRP is required")
    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "Invalid MRP format")
    @Column(nullable = false)
    private String mrp;

    @NotBlank(message = "Sale price is required")
    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "Invalid sale price format")
    @Column(nullable = false)
    private String salePrice;

    private String baseCost;

    @NotBlank(message = "Status is required")
    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String lastUpdateDate;

    private String brand;
    private String color;
    private String size;
    private String weight;
    private String length;
    private String width;
    private String height;
    private String serialTracking;
    private String sellerId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
