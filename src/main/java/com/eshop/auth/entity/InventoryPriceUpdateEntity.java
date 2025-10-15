package com.eshop.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inventory_price_updates")
public class InventoryPriceUpdateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String sku;
    
    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer invQty;
    
    @NotBlank
    @Column(nullable = false)
    private String sellerId;
    
    @NotBlank
    @Column(nullable = false)
    private String token;
    
    private Long moderationId;
    
    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer requestedInvQty;
    
    private Integer approvedInvQty;
    
    @NotBlank
    @Column(nullable = false)
    private String status;
    
    private Double requestedSalePrice;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
