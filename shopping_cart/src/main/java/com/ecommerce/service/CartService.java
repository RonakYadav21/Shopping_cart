package com.ecommerce.service;

import java.util.List;

import com.ecommerce.model.Cart;

public interface CartService {

	public Cart saveCart(int productId,Integer userId);
	
	public List<Cart> getcartsByUser(Integer userid);
	public Integer getCountCart(Integer userId);

	public void updateQuantity(String sy, Integer cid);

	public void deletecart(Integer id);
}
