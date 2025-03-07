package com.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.model.ProductOrder;

public interface ProductOrderRepository extends JpaRepository<ProductOrder,Integer> {

	 public List<ProductOrder> findByUserId(Integer userid);
     public ProductOrder findByOrderId(String orderId);

}
