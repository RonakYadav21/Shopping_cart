package com.ecommerce.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Category;
import com.ecommerce.model.OrderRequest;
import com.ecommerce.model.ProductOrder;
import com.ecommerce.model.UserDlt;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.service.CartService;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.userService;
import com.ecommerce.util.CommonUtil;
import com.ecommerce.util.OrderStatus;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user") //the "user" refers to a base URL path for the controller
public class UserController {
	
	@Autowired
	private userService userservice;
	
	@Autowired
	CategoryService categoryservice;

	@Autowired
   private CartService cartservice;
	
	@Autowired
	private OrderService orderservice;
	
	@Autowired 
	private CommonUtil commonutils;
	
	@Autowired
	private PasswordEncoder passwordencoder;
@GetMapping("/")	
public String home()
	{
		return "user/home"; //return "user/home";, user is typically a folder (or directory) name 
	}


@ModelAttribute  //whene controller is calleld this method will be automatically caLLED
public void getUserDetails(Principal p,Model m) {
 if(p!=null) {
	 String email=p.getName();
	UserDlt userDtl= userservice.getUserByEmail(email);
	 m.addAttribute("user", userDtl);
	  Integer cartcount= cartservice.getCountCart(userDtl.getId());
	  m.addAttribute("countcart", cartcount);

 }
	List<Category> allcategories=categoryservice.getAllActive();
	m.addAttribute("category", allcategories);

}

@GetMapping("/addCart")
public String addToCart(@RequestParam int pid, @RequestParam int uid, HttpSession session) {
    Cart saveCart = cartservice.saveCart(pid, uid);
    System.out.println("Saved Cart: " + saveCart);

    if (ObjectUtils.isEmpty(saveCart)) {
        session.setAttribute("errormessage", "Product add to cart failed");
    } else {
        session.setAttribute("successMsg", "Product added to cart");
    }
    return "redirect:/productdetail/" + pid;
}
@GetMapping("/cart")
public String loadCartpage(Principal p,Model m) {
	UserDlt user=getLoggedInUserDetails(p);
	 List<Cart>carts=cartservice.getcartsByUser(user.getId());
	 m.addAttribute("carts", carts);
	 if(carts.size()>0) {
	 Double totalOrderPrice=carts.get(carts.size()-1).getTotalOrderAmount();
	 m.addAttribute("totalorderprice",totalOrderPrice);
	 }
	return "user/cart"; 
}

private UserDlt getLoggedInUserDetails(Principal p) {
	String email=p.getName();
	UserDlt userDlt=userservice.getUserByEmail(email);
	return userDlt;
}

@GetMapping("/cartUpdate")
public String updateCartQuatity(@RequestParam String sy,@RequestParam Integer cid ) {
	 cartservice.updateQuantity(sy,cid);
	return "redirect:/user/cart";
}

@GetMapping("/orders")
public String oderPage(Principal p,Model m) {
	 UserDlt user=getLoggedInUserDetails(p);
	 List<Cart>carts=cartservice.getcartsByUser(user.getId());
//	 m.addAttribute("carts", carts);
	 if(carts.size()>0) {
	  Double totalprice=carts.get(carts.size()-1).getTotalOrderAmount();
	  Double totalOrderPrice=carts.get(carts.size()-1).getTotalOrderAmount()+100+50;
	   m.addAttribute("totalorderprice",totalOrderPrice);
	   m.addAttribute("TotalPrice", totalprice);
	 }
	return "/user/order";
}

@PostMapping("/save-order/{id}")
public String saveoderPage(@PathVariable("id") Integer userid,@ModelAttribute OrderRequest request) {
	System.out.println(request);
	orderservice.saveOrder(userid, request);
	System.out.println("order place");
	return "redirect:/user/success";
}

@GetMapping("/success")
public String successpage(Principal p,Model m) {
	 UserDlt user=getLoggedInUserDetails(p);
	 cartservice.deletecart(user.getId());
	  Integer cartcount= cartservice.getCountCart(user.getId());
	  m.addAttribute("countcart", cartcount);

	return "/user/success";
}

@GetMapping("/user-orders")
public String userorder(Model m,Principal p) {
	 UserDlt user=getLoggedInUserDetails(p);
	 List<ProductOrder>orders= orderservice.getOrderByUser(user.getId());
	 m.addAttribute("userorder", orders);
	return "user/my_order";
}
@GetMapping("/update-status")
public String updatestatus(@RequestParam("id") Integer id,@RequestParam("st") Integer st , HttpSession session ) throws UnsupportedEncodingException, MessagingException {
	System.out.println(" hello values");

	OrderStatus[] values = OrderStatus.values();
    System.out.println("Available OrderStatus values: " + Arrays.toString(values));
	String status = null;

	for (OrderStatus orderSt : values) {
		if (orderSt.getId().equals(st)) {
			status = orderSt.getName();
			System.out.println("status"+status);
		}
	}

	ProductOrder updateOrder = orderservice.updateOrderStatus(id, status);
	System.out.println("statusupdate"+updateOrder);
	commonutils.SendMailForProductOrder(updateOrder, status);

	if (!ObjectUtils.isEmpty(updateOrder)) {
		session.setAttribute("successMsg", "Status Updated");
	} else {
		session.setAttribute("errormessage", "status not updated");
	}
	return "redirect:/user/user-orders";
}

@GetMapping("/profile")
public String profile() {
	return  "user/profile";
}

@PostMapping("update-profile")
public String updateProfile(@ModelAttribute UserDlt user,@RequestParam MultipartFile img,HttpSession session) {
	 UserDlt updateuser=userservice.updateuserProfile(user, img);
	if(!ObjectUtils.isEmpty(updateuser)) {
		session.setAttribute("successMsg", " profile Status Updated");
	} else {
		session.setAttribute("errormessage", " profile status not updated");
	}
	return "redirect:/user/profile";
}
@PostMapping("/change-password")
public String hangePassword(@RequestParam String newPassword,@RequestParam String currentpassword,Principal p,HttpSession session) {
	UserDlt loggeduser=getLoggedInUserDetails(p);
	 boolean matches=passwordencoder.matches(currentpassword, loggeduser.getPassword());
    if(matches) {
    	String encodepass=passwordencoder.encode(newPassword);
    	loggeduser.setPassword(encodepass);
    	 UserDlt updateuser=userservice.updateUser(loggeduser);
    	
    	if(ObjectUtils.isEmpty(updateuser)) {
    		session.setAttribute("errormessage", " Something Went Wrong !!");

    	}
    	else {
    		session.setAttribute("successMsg", " password changed successfully ");

    	}
    	
    }
    else {
        session.setAttribute("errormessage", "Current Password is incorrect");
        System.out.println("currrent password is incorrect");

    }
    		
	return "redirect:/user/profile";
}
}
