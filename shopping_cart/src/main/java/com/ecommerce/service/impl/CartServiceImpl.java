package com.ecommerce.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Product;
import com.ecommerce.model.UserDlt;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartrepository;
	
	@Autowired
	private UserRepository userrepositoy;
	
	@Autowired
	private ProductRepository productrepository;
	
	@Override
	public Cart saveCart(int productId, Integer userId) {
	    // Check if the user and product exist
	    Optional<UserDlt> userOptional = userrepositoy.findById(userId);
	    Optional<Product> productOptional = productrepository.findById(productId);

	    if (userOptional.isEmpty() || productOptional.isEmpty()) {
	        throw new RuntimeException("User or Product not found");
	    }

	    UserDlt user = userOptional.get();
	    Product product = productOptional.get();

	    // Check if the product is already in the cart
	    Cart cartStatus = cartrepository.findByProductIdAndUserId(productId, userId);
	    Cart cart;

	    if (cartStatus == null) {  // First time adding the product to the cart
	        cart = new Cart();
	        cart.setProduct(product);
	        cart.setUser(user);
	        cart.setQuantity(1);
	        cart.setTotalPrice(product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice());
	    } else {  // Product already in the cart, increase quantity
	        cart = cartStatus;
	        cart.setQuantity(cart.getQuantity() + 1);
	        Double discountPrice = (product.getDiscountPrice() != null) ? product.getDiscountPrice() : product.getPrice();
	        cart.setTotalPrice(cart.getQuantity() * discountPrice);
	    }

	    return cartrepository.save(cart);
	}

	@Override
	public List<Cart> getcartsByUser(Integer userid) {
		 List<Cart>carts =cartrepository.findByUserId(userid)	;
		 Double totalOrderAmount=0.0;
		 List<Cart>updatecarts=new ArrayList<>();
		 for(Cart c:carts) {
			 Double totalPrice=(c.getProduct().getDiscountPrice()*c.getQuantity());
			c.setTotalPrice(totalPrice);
			totalOrderAmount=totalOrderAmount+totalPrice;
			c.setTotalOrderAmount(totalOrderAmount);
			updatecarts.add(c);
		 }
		return updatecarts;
	}

	@Override
	public Integer getCountCart(Integer userId) {
		  Integer countbyuserid= cartrepository.countByUserId(userId);
		  System.out.println(countbyuserid);
		return countbyuserid;
	}

	@Override
	public void updateQuantity(String sy, Integer cid) {
	Cart cart=cartrepository.findById(cid).get();
	int updatequantity;
	if(sy.equalsIgnoreCase("de")) {
		 updatequantity=cart.getQuantity()-1;
		 if(updatequantity<=0) {
			 cartrepository.delete(cart);
		 }else {
				cart.setQuantity(updatequantity);
				 cartrepository.save(cart);
		 }	 	 
	}else {
		updatequantity=cart.getQuantity()+1;
		cart.setQuantity(updatequantity);
		cartrepository.save(cart);
	}
	}

	@Override
	public void deletecart(Integer id) {
		 List<Cart>carts =cartrepository.findByUserId(id)	;
		    cartrepository.deleteAll(carts); // Deletes all cart items for the user

	}

}
