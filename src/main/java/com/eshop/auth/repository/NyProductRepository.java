package com.eshop.auth.repository;

import com.eshop.auth.entity.NyProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface NyProductRepository extends JpaRepository<NyProduct, Long> {
    
    @Query("SELECT p FROM NyProduct p WHERE p.lastUpdateDate >= :updatedDate AND p.token = :token")
    Page<NyProduct> findByUpdatedDateAndToken(
            @Param("updatedDate") String updatedDate,
            @Param("token") String token,
            Pageable pageable);
    
    @Query("SELECT p FROM NyProduct p WHERE p.sku IN :skuCodes AND p.token = :token")
    Page<NyProduct> findBySkuCodesAndToken(
            @Param("skuCodes") List<String> skuCodes,
            @Param("token") String token,
            Pageable pageable);
    
    @Query("SELECT p FROM NyProduct p WHERE p.lastUpdateDate >= :updatedDate AND p.sku IN :skuCodes AND p.token = :token")
    Page<NyProduct> findByUpdatedDateAndSkuCodesAndToken(
            @Param("updatedDate") String updatedDate,
            @Param("skuCodes") List<String> skuCodes,
            @Param("token") String token,
            Pageable pageable);
            
    @Query("SELECT p FROM NyProduct p WHERE p.token = :token")
    Page<NyProduct> findAllByToken(@Param("token") String token, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<NyProduct> findBySkuAndToken(String sku, String token);

    boolean existsBySkuAndToken(String sku, String token);

    @Query("SELECT COUNT(p) FROM NyProduct p WHERE p.token = :token")
    long countByToken(@Param("token") String token);

    @Query("SELECT p FROM NyProduct p WHERE p.token = :token AND p.status = :status")
    Page<NyProduct> findByTokenAndStatus(
            @Param("token") String token,
            @Param("status") String status,
            Pageable pageable);
}
