package com.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.model.OrderAddress;

public interface orderAddressRepository extends JpaRepository<OrderAddress,Integer> {

}
