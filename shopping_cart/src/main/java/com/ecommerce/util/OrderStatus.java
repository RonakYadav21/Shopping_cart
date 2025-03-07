package com.ecommerce.util;

//In Java, an enum (short for enumeration) is a special data type that represents a fixed set of constants. It is used when you have a variable that should only take one of a limited number of possible values.


public enum OrderStatus {
    IN_PROGRESS(1, "In Progress"),
    ORDER_RECEIVED(2, "Order Received"),  // Fixed spelling from "Recived" to "Received"
    ORDER_PACKED(3, "Product Packed"),
    OUT_FOR_DELIVERY(4, "Out for Delivery"),
    DELIVERED(5, "Delivered"),
    CANCEL(6, "Cancel"),
    SUCCESS(7,"Success");
	private Integer id;
	private String name;
	private OrderStatus(Integer id,String name) {
		this.id=id;
		this.name=name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
