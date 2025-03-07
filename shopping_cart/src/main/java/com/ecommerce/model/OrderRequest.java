package com.ecommerce.model;

import lombok.Data;
//1. OrderRequest (Data Transfer Object - DTO)
//It acts as a DTO (Data Transfer Object) that receives order details from the frontend.
//Does not represent a database entity—it is only used to transfer data between the client and server.

@Data
public class OrderRequest {
	private String firstName;
	private String lastName;
	private String email;
	private String mobileNo;
	private String Address;
	private String city;
	private String state;
	private String pincode;
   private String paymentType;

	
}
