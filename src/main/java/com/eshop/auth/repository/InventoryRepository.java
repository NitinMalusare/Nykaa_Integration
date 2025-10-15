package com.eshop.auth.repository;

import com.eshop.auth.entity.InventoryPriceUpdateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryPriceUpdateEntity, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InventoryPriceUpdateEntity> findBySkuAndSellerIdAndToken(String sku, String sellerId, String token);
    
    @Query("SELECT i FROM InventoryPriceUpdateEntity i WHERE i.token = :token")
    List<InventoryPriceUpdateEntity> findAllByToken(@Param("token") String token);
    
    @Query("SELECT i FROM InventoryPriceUpdateEntity i WHERE i.sku IN :skus AND i.token = :token")
    List<InventoryPriceUpdateEntity> findBySkusAndToken(@Param("skus") List<String> skus, @Param("token") String token);
    
    boolean existsBySkuAndSellerIdAndToken(String sku, String sellerId, String token);
}
