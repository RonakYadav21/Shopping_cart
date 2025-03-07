package com.ecommerce.service.impl;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecommerce.model.Cart;
import com.ecommerce.model.OrderAddress;
import com.ecommerce.model.OrderRequest;
import com.ecommerce.model.ProductOrder;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductOrderRepository;
import com.ecommerce.service.OrderService;
import com.ecommerce.util.CommonUtil;
import com.ecommerce.util.OrderStatus;

@Service
public class OderServiceImpl implements OrderService {

	@Autowired
	private ProductOrderRepository orderrepository;
	
	@Autowired
	private CartRepository cartrepository;
	
	@Autowired
	private CommonUtil commonutils;
	
	
	@Override
	public void saveOrder(Integer userid,OrderRequest orderRequest) {
     List<Cart>carts=cartrepository.findByUserId(userid);
     for(Cart cart:carts) {
    	 ProductOrder order=new ProductOrder();
    	 order.setOrderId(UUID.randomUUID().toString());
    	 order.setOrderDate(new Date(System.currentTimeMillis()));
    	 order.setProduct(cart.getProduct());
    	 order.setPrice(cart.getProduct().getDiscountPrice());
    	 order.setQuatity(cart.getQuantity());
    	 order.setUser(cart.getUser());
    	 order.setStatus(OrderStatus.IN_PROGRESS.getName());
    	 order.setPaymentType(orderRequest.getPaymentType());
    	 OrderAddress address=new OrderAddress();
    	 address.setFirstName(orderRequest.getFirstName());
    	 address.setLastName(orderRequest.getLastName());
    	 address.setEmail(orderRequest.getEmail());
    	 address.setMobileNo(orderRequest.getMobileNo());
    	 address.setAddress(orderRequest.getAddress());
    	 address.setCity(orderRequest.getCity());
    	 address.setState(orderRequest.getState());
    	 address.setPincode(orderRequest.getPincode());
    	 order.setOrderAddress(address);
    	  ProductOrder saveorder= orderrepository.save(order);
    	 try {
    	 commonutils.SendMailForProductOrder(saveorder, "success");
    	 }catch(Exception e) {
    		 e.printStackTrace();
    	 }
     }
	
}

	@Override
	public List<ProductOrder> getOrderByUser(Integer userId) {
	 List<ProductOrder> orders=	orderrepository.findByUserId(userId);
		return orders;
	}

	@Override
	public ProductOrder updateOrderStatus(Integer id, String status) {
	    Optional<ProductOrder> findBy = orderrepository.findById(id); // Here, id is the order ID
	    if (findBy.isPresent()) { // Corrected condition
	        ProductOrder productOrder = findBy.get(); // Retrieve the object
	        productOrder.setStatus(status);
	       ProductOrder updateorder=  orderrepository.save(productOrder); // Save the updated order
	        return updateorder ;
	    }
	    
	    return null; // If the order was not found, return false
	}

	@Override
	public List<ProductOrder> getAllOrder() {
       return orderrepository.findAll() ;
	}

	@Override
	public ProductOrder getordersByOrderId(String orderId) {
				 ProductOrder order=orderrepository.findByOrderId(orderId);
				return order;
	}
	
	
}
