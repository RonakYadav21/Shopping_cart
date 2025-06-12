package com.ecommerce.model;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//Database Entity - Stores Order Details
//Represents an actual order in the database.
//Links a user, product, and shipping address.

@Entity
@Setter
@Getter
@NoArgsConstructor
public class ProductOrder {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
private Integer id;
private String orderId;
private Date orderDate;
@ManyToOne
private Product product;
private Double price;
private Integer quatity;

@ManyToOne//One user can have multiple orders, but each order belongs to only one user.

private UserDlt user; 
private String status;
private String paymentType;

@OneToOne(cascade = CascadeType.ALL)//🔹 Spring Boot and Hibernate will generate a foreign key (order_address_id) in the product_order table that references the id column of order_address.
private OrderAddress orderAddress;

}

