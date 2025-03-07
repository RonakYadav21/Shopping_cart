package com.ecommerce.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.model.UserDlt;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.CartService;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.userService;
import com.ecommerce.util.CommonUtil;

import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class Homecontroller {

	@Autowired
	CategoryService categoryservice;
	@Autowired
	private ProductService productservice;
	@Autowired
	private userService userservice;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private CartService cartservice;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
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
	
    @GetMapping("/")
    public String hello(Model m) {
     	List<Category> allcategories=categoryservice.getAllActive().stream().sorted((c1, c2) -> Integer.compare(c2.getId(), c1.getId())) // Using Integer.compare()
.limit(6).toList();
     	m.addAttribute("category", allcategories);
     	List<Product> allproducts = productservice.getAllActiveProduct("")
     		    .stream()
     		    .sorted((p1, p2) -> Integer.compare(p2.getId(), p1.getId())) // Using Integer.compare()
     		    .limit(6)
     		    .toList();
     	m.addAttribute("product", allproducts);

        return "index"; // This returns "index.html" from the templates folder.
    }
    @GetMapping("/index")
    public String helloindex(Model m) {
     	List<Category> allcategories=categoryservice.getAllActive().stream().sorted((c1, c2) -> Integer.compare(c2.getId(), c1.getId())) // Using Integer.compare()
.limit(6).toList();
     	m.addAttribute("category", allcategories);
     	List<Product> allproducts = productservice.getAllActiveProduct("")
     		    .stream()
     		    .sorted((p1, p2) -> Integer.compare(p2.getId(), p1.getId())) // Using Integer.compare()
     		    .limit(6)
     		    .toList();
     	m.addAttribute("product", allproducts);
        return "index"; // This returns "index.html" from the templates folder.
    }


    @GetMapping("/signin")
    public String loginpage() {
        return "login";
    }
    
    @GetMapping("/register")
    public String Registerpage() {
        return "register"; 
    }
    
    @GetMapping("/product")
    public String productpage(Model m,@RequestParam(value="category",defaultValue="")String category,@RequestParam(name="pageNo",defaultValue="0") Integer pageNo,@RequestParam(name="pageSize",defaultValue="8")Integer pageSize,@RequestParam(name="ch",defaultValue="")String ch) {
    	  List<Category> categories =categoryservice.getAllActive();
    	  m.addAttribute("category", categories);
    	m.addAttribute("paramvalue", category);
    	Page<Product> page=null;
    	if(StringUtils.isEmpty(ch)) {
       	 page=productservice.getAllActiveProductPagination(pageNo,pageSize,category);
    	}else {
    		 page=productservice.searchproductActiveProductPagination(pageNo,pageSize,category,ch);
    	}
    	  List<Product> product=page.getContent();
    	  m.addAttribute("productSize", product.size());
    	m.addAttribute("product", product);
    	m.addAttribute("pageNo", page.getNumber());
    	m.addAttribute("pageSize", pageSize);
    	m.addAttribute("totalElement", page.getTotalElements());
    	m.addAttribute("totalPages", page.getTotalPages());
    	m.addAttribute("isFirst", page.isFirst());
    	m.addAttribute("isLast", page.isLast());
        return "product"; // This returns "product.html" from the templates folder.
    }

    @GetMapping("/productdetail/{id}")
    public String productdetailpage(Model m,@PathVariable Integer id , HttpSession session) {
    	System.out.println(id);
        if (id == null) {
            session.setAttribute("Msg", "Invalid product ID");
            return "redirect:/error"; // Redirect to an error page or handle accordingly
        }

    	  Product p=productservice.getProductById(id);
    	  System.out.println(p);
    	  if(!ObjectUtils.isEmpty(p)) {
                m.addAttribute("productdetail",p) 	;
    	  }
    	  else {
    		  session.setAttribute("errormessage", "product not found");
    	  }
    	
        return "productdetail"; // This returns "detaildetail.html" from the templates folder.
    }
    
    
    
    //register the user or save user details
    @PostMapping("/saveuser")
    public String saveuser(@ModelAttribute UserDlt user ,@RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
    	Boolean existsEmail=userservice.existsEmail(user.getEmail());
    	  String imageName= file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
    	  user.setProfileImage(imageName);
    	  UserDlt saveuser=  userservice.saveUser(user);
    	  if(existsEmail) {
    		  session.setAttribute("errormessage", "email already exist");
    	  }else {
    		  
    	  if(!ObjectUtils.isEmpty(saveuser)) {
    		    if(!file.isEmpty()) {
    				String uploadDir = "C:/shopping_cart/images/category_img/"; // Change to a folder outside your project
    				File directory = new File(uploadDir);
    				if (!directory.exists()) {
    				    directory.mkdirs(); // Create directory if it doesn't exist
    				}

    				Path path = Paths.get(uploadDir + file.getOriginalFilename());
    				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    		    }
    		    
      		  session.setAttribute("successMsg", "user registered successfully");

    	  }
    	  else {
    		  session.setAttribute("errormessage", "something went wrong");
    	  }
    	  }
    	return "redirect:/register";
    }
    

    
    //forgot password
    @GetMapping("/forgot_password")
    public String showForgotPassword() {
    	return "Forgot_password";
    	
    }
    
    @PostMapping("/forgot_password")
    public String processForgotPassword(@RequestParam String email,HttpSession session,HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
    	UserDlt user=userservice.getUserByEmail(email);
    	if(ObjectUtils.isEmpty(user)) {
    		System.out.println("error");
    		session.setAttribute("errormessage","invalid email");
    	}else {
    		//UUID.randomUUID().toString() is a Java method that generates a Universally Unique Identifier (UUID). Here's a detailed breakdown:


    		String resetToken=UUID.randomUUID().toString();// tostring  Converts the UUID object into a string representation.

    		userservice.updateUserResetToken(email,resetToken);
    		//Generate URL :http://localhost:8080/reset_password?tokens=sddfdkdkrejkr
    	String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

    		Boolean sendmail=commonUtil.SendMail(url,email);
    		//successMsg
    		if(sendmail) {
        		session.setAttribute("successMsg"," Email password Reset Link is Sent email");

    		}else {
        		session.setAttribute("errormessage","Something Went Wrong");

    		}
    	}
    	return "redirect:/forgot_password";
    	
    }

    
    @GetMapping("/reset-password")
    public String showresetPassword(@RequestParam String token,HttpSession session,Model m) {
    	 UserDlt user=userservice.getUserByToken(token);
    	 if(ObjectUtils.isEmpty(user)) {
           m.addAttribute("errormessage","invalid token or token expired")  ;
       return "error";
    	 }
    		m.addAttribute("token",token) ;
    	return "reset_password";
    	
    }

    
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String Password,HttpSession session,Model m) {
    	 UserDlt usertoken=userservice.getUserByToken(token);
    	 
    	 if(ObjectUtils.isEmpty(usertoken)) {
             m.addAttribute("Msg","invalid token or token expired")  ;

       return "error";
    	 }else {
    		 usertoken.setPassword(passwordEncoder.encode(Password));
    		 usertoken.setResetToken(null);
    		 userservice.updateUser(usertoken);
//    		 session.setAttribute("succMsg", "Password change successfully");
             m.addAttribute("Msg","Password change successfully")  ;

    		 return "error";
    	 } 		 
    }
    
//    @GetMapping("/search")
//    public String searchProduct(@RequestParam String ch,Model m) {
//    	List<Product> searchProduct=productservice.searchProduct(ch);
//    	m.addAttribute("product", searchProduct);
// 	List<Category> category=categoryservice.getAllActiveCategory();
// 	m.addAttribute("category", category);
//    	return "product";
//    }

}
