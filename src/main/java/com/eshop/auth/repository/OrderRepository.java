package com.eshop.auth.repository;

import com.eshop.auth.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Find orders by seller type
     */
    List<Order> findBySellerType(String sellerType);
    
    /**
     * Find orders by seller ID
     */
    List<Order> findBySellerId(String sellerId);
    
    /**
     * Find orders by seller type and seller ID
     */
    List<Order> findBySellerTypeAndSellerId(String sellerType, String sellerId);
    
    /**
     * Find orders updated after a specific date
     */
    List<Order> findByOrderLastUpdateDateAfter(LocalDateTime updateDate);
    
    /**
     * Find orders by status list
     */
    @Query("SELECT o FROM Order o WHERE o.orderStatus IN :statusList")
    List<Order> findByOrderStatusIn(@Param("statusList") List<String> statusList);
    
     // Find orders with complex filtering
     
    @Query("SELECT o FROM Order o WHERE " +
           "(:sellerType IS NULL OR o.sellerType = :sellerType) AND " +
           "(:sellerId IS NULL OR o.sellerId = :sellerId) AND " +
           "(:updateDate IS NULL OR o.orderLastUpdateDate >= :updateDate) AND " +
           "(:statusList IS NULL OR o.orderStatus IN :statusList) " +
           "ORDER BY o.orderLastUpdateDate ASC")
    List<Order> findOrdersWithFilters(
            @Param("sellerType") String sellerType,
            @Param("sellerId") String sellerId,
            @Param("updateDate") LocalDateTime updateDate,
            @Param("statusList") List<String> statusList
    );
    
    /**
     * Find orders by seller type with complex filtering
     */
    @Query("SELECT o FROM Order o WHERE o.sellerType = :sellerType AND " +
           "(:sellerId IS NULL OR o.sellerId = :sellerId) AND " +
           "(:updateDate IS NULL OR o.orderLastUpdateDate >= :updateDate) AND " +
           "(:statusList IS NULL OR o.orderStatus IN :statusList) " +
           "ORDER BY o.orderLastUpdateDate ASC")
    List<Order> findOrdersBySellerTypeWithFilters(
            @Param("sellerType") String sellerType,
            @Param("sellerId") String sellerId,
            @Param("updateDate") LocalDateTime updateDate,
            @Param("statusList") List<String> statusList
    );
}
