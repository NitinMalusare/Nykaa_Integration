package com.eshop.auth.repository;

import com.eshop.auth.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderNo(String orderNo);
    
    List<OrderItem> findByOrderNoIn(List<String> orderNos);
}

