package com.ecommerce.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.model.ProductOrder;
import com.ecommerce.model.UserDlt;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.userService;
import com.ecommerce.util.CommonUtil;
import com.ecommerce.util.OrderStatus;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class Admincontroller {

	@Autowired
	private CategoryService categoryservice;
	
	@Autowired
	private userService userservice;
	
	@Autowired
	private ProductService productservice;
	
	@Autowired
	 private OrderService orderservice;
	
	@Autowired
	CommonUtil commonutils;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;


	@GetMapping("/")
	public String index() {
		return "admin/index";
	}
	
	
	@ModelAttribute  //whene controller is calleld this method will be automatically caLLED
	public void getUserDetails(Principal p,Model m) {
	 if(p!=null) {
		 String email=p.getName();
		UserDlt userDtl= userservice.getUserByEmail(email);
		 m.addAttribute("user", userDtl);
	 }
		List<Category> allcategories=categoryservice.getAllActive();
		m.addAttribute("category", allcategories);

	}

	@GetMapping("/index")
	public String indexpage() {
		return "admin/index";
	}


	
	@GetMapping("/category")
	public String category(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		// m.addAttribute("categorys", categoryService.getAllCategory());
		Page<Category> page = categoryservice.getAllCategoryPagination(pageNo, pageSize);
		List<Category> categorys = page.getContent();
		m.addAttribute("categorys", categorys);

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());
	
		return "admin/category";
	}

	
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {
System.out.println("added category "+category.getId());
		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);

		Boolean existCategory = categoryservice.existCategory(category.getName());

		if (existCategory) {
			session.setAttribute("errormessage", "Category Name already exists");
		} else {

			Category saveCategory = categoryservice.saveCategory(category);

			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errormessage", "Not saved ! internal server error");
			} else {

				String uploadDir = "C:/shopping_cart/images/category_img/"; // Change to a folder outside your project
				File directory = new File(uploadDir);
				if (!directory.exists()) {
				    directory.mkdirs(); // Create directory if it doesn't exist
				}

				Path path = Paths.get(uploadDir + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				session.setAttribute("successMsg", "Saved successfully");
			}
		}

		return "redirect:/admin/category";
	}


	
	
	
	
	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id ,HttpSession session) {
		Boolean deletecategory=categoryservice.deleteCategory(id);
		if(deletecategory) {
			session.setAttribute("successMsg","category delete successfully");
		}
		else {
			session.setAttribute("errormessage","something went wrong on server");

		}
		return"redirect:/admin/category";
	}
	
	@GetMapping("/loadEditCategory/{id}")
	public String editcatgory(@PathVariable int id ,Model m) {
		m.addAttribute("category",categoryservice.getCategoryById(id));
		return "/admin/edit_category";
	}
	
	@PostMapping("/edit_categoryitem")
	public String edit_category(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException{
		      Category existingCategory=categoryservice.getCategoryById(category.getId());
		      
		      if (existingCategory == null) {
		          session.setAttribute("errormessage", "Category not found");
		    	  return "/admin/edit_category"; 
		      }

		    if (categoryservice.existCategory(category.getName()) && !existingCategory.getName().equals(category.getName())) {
		        session.setAttribute("errormessage", "Category already exists with the same name");
		  	  return "/admin/edit_category"; 
		    }

		    
			String imageName = file.isEmpty() ? existingCategory.getImageName() : file.getOriginalFilename();
		    category.setImageName(imageName);
		    
		    Category updatedCategory = categoryservice.saveCategory(category);

			if(ObjectUtils.isEmpty(updatedCategory)) {
				session.setAttribute("errormessage", " not saved ! internal server error");	
			}
			else {
				String uploadDir = "C:/shopping_cart/images/category_img/"; // Change to a folder outside your project
				File directory = new File(uploadDir);
				if (!directory.exists()) {
				    directory.mkdirs(); // Create directory if it doesn't exist
				}

				Path path = Paths.get(uploadDir + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
		
	  return "/admin/edit_category"; 
	}
	
//	product  add
	
	@GetMapping("/loadaddproduct")
	public String loadaddproduct(Model m) {
		m.addAttribute("categorys",categoryservice.getAllCategory());
		return "admin/addproduct";
	}
	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, HttpSession session) throws IOException { //@modelattribute  Binds data (from a request, method return, or form) to an object and adds it to the model.
       product.setDiscount(0);
       product.setDiscountPrice(product.getPrice());
//       product.setActive(product.isActive());
			String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
		 product.setImage(imageName);
		if(productservice.existProduct(product.getTitle())) {
			session.setAttribute("errormessage", "Product Already exist");

		}
		else {
			
		Product saveproduct=productservice.saveProduct(product);
		if(ObjectUtils.isEmpty(saveproduct)) {
			session.setAttribute("errormessage", " not saved ! internal server error");
		}
		else {
			String uploadDir = "C:/shopping_cart/images/product_img/"; // Change to a folder outside your project
			File directory = new File(uploadDir);
			if (!directory.exists()) {
			    directory.mkdirs(); // Create directory if it doesn't exist
			}

			Path path = Paths.get(uploadDir + image.getOriginalFilename());
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			 System.out.println(path);
			session.setAttribute("successMsg", " Product saved");

		}
		}
		return "redirect:/admin/loadaddproduct";
	}

	@GetMapping("/products")
	public String products(Model m,@RequestParam(name="pageNo",defaultValue="0") Integer pageNo, @RequestParam(name="pageSize",defaultValue="5")Integer pageSize,String category) {
	      Page<Product>page=productservice.getAllActiveProductPagination(pageNo, pageSize,category);
	      List<Product>product=page.getContent();
			m.addAttribute("products",product);
			m.addAttribute("productSize", product.size());
	    	m.addAttribute("pageNo", page.getNumber());
	    	m.addAttribute("pageSize", pageSize);
	    	m.addAttribute("totalElement", page.getTotalElements());
	    	m.addAttribute("totalPages", page.getTotalPages());
	    	m.addAttribute("isFirst", page.isFirst());
	    	m.addAttribute("isLast", page.isLast());

		
		
		
		return "admin/product";
	}

//	delete product
	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id ,HttpSession session) {
		Boolean deleteproduct=productservice.deleteProduct(id);
		if(deleteproduct) {
			session.setAttribute("successMsg","product delete successfully");
		}
		else {
			session.setAttribute("errormessage","something went wrong on server");

		}
		return"/admin/product";
	}

	
//	update Product
	@GetMapping("/loadEditProduct/{id}")
	public String editproduct(@PathVariable int id ,Model m) {
		m.addAttribute("product",productservice.getProductById(id));
		m.addAttribute("categorys",categoryservice.getAllCategory());

		return "/admin/edit_product";
	}
	
	@PostMapping("/edit_productitem")
	public String editproductdetail(@ModelAttribute Product product, @RequestParam("file") MultipartFile file, HttpSession session)throws IOException {
		Product existproduct=productservice.getProductById(product.getId());
		if(existproduct==null) {
	          session.setAttribute("errormessage", "Category not found");
	    	  return "/admin/edit_product"; 
		}
	    

		String imagename=file!=null?file.getOriginalFilename():existproduct.getImage();
		product.setImage(imagename);
		
		existproduct.setDiscount(product.getDiscount());
		double discount=product.getPrice()*(product.getDiscount()/100.0);
		 double discountprice=product.getPrice()-discount;
		 product.setDiscountPrice(discountprice);

		 if(product.getDiscount()<0 || product.getDiscount()>100) {
				session.setAttribute("errormessage", " invalid discount");

		 }
		 else {
			 
				Product updatedProduct = productservice.saveProduct(product);
		if(ObjectUtils.isEmpty(updatedProduct)) {
			session.setAttribute("errormessage", " not saved ! internal server error");

		}
		
		else {
			String uploadDir = "C:/shopping_cart/images/product_img/"; // Change to a folder outside your project
			File directory = new File(uploadDir);
			if (!directory.exists()) {
			    directory.mkdirs(); // Create directory if it doesn't exist
			}

			Path path = Paths.get(uploadDir + file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}
	
		 }

		return "admin/edit_product";
	}
	
	
	//view users
	@GetMapping("/Users")
	public String users(Model m, HttpSession session) {
	    List<UserDlt> users = userservice.getAllUsers("ROLE_USER");

	    System.out.println("Fetched Users: " + users); // Debugging line

	    m.addAttribute("users", users);
	    return "/admin/Users";
	}

	@GetMapping("/updateStatus")
	public String updateUserAccountStatus(@RequestParam Boolean status,@RequestParam Integer id,HttpSession session) {
//		System.out.println("Received update request for user ID: " + id + ", Status: " + status);
		Boolean f=userservice.updateAccountStatus(id,status);

		if(f)
		{
			session.setAttribute("succMsg", "Account update");
		}
		else {
			session.setAttribute("errormessage", "Account  not update somthing went wrong");

		}
		return "redirect:/admin/Users";
	}

	@GetMapping("/allorders")
	public String getAllorders(Model m) {
		 List<ProductOrder>allorders =orderservice.getAllOrder();
		 System.out.println(allorders);
		 m.addAttribute("orders", allorders);
	    	m.addAttribute("srch", false);

		return "/admin/orders";
	}
	
	
	
	
	
	
	
	@PostMapping("/update-order-status")
	public String updatestatus(@RequestParam("id") Integer id,@RequestParam("st") Integer st , HttpSession session ) throws UnsupportedEncodingException, MessagingException {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		ProductOrder updateOrder = orderservice.updateOrderStatus(id, status);
		commonutils.SendMailForProductOrder(updateOrder, status);
		System.out.println("password  "+status);
		
		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("successMsg", "Status Updated");
		} else {
			session.setAttribute("errormessage", "status not updated");
		}
		return "redirect:/admin/allorders";
	}
	
    @GetMapping("/order-search")
    public String searchProduct(@RequestParam String orderId,Model m,HttpSession session) {
    	if(orderId!=null) {
    	ProductOrder Product=orderservice.getordersByOrderId(orderId.trim());
        if (Product == null || Product.getOrderId() == null) { 
            session.setAttribute("errormessage", "Incorrect Order ID");
            m.addAttribute("o", null);
        }
    	else {
    	m.addAttribute("o", Product);
    	}
    	m.addAttribute("srch", true);
    	}else {
   		 List<ProductOrder>allorders =orderservice.getAllOrder();
   		 m.addAttribute("orders", allorders);
   	    	m.addAttribute("srch", false);

    	}
    	return "/admin/orders";
    }

 @GetMapping("/add-admin")
 public String loadadminAdd() {
	 return "/admin/add_admin";
 }
  
 @PostMapping("/save-admin")
 public  String loadadminpage(@ModelAttribute UserDlt admin ,@RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
	  String imageName= file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
	  admin.setProfileImage(imageName);
	  UserDlt saveuser=  userservice.saveAdmin(admin);
	  if(!ObjectUtils.isEmpty(saveuser)) {
		    if(!file.isEmpty()) {
		    	File saveFile=new ClassPathResource("static/image").getFile();
		    	Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+"profile_img"+File.separator+file.getOriginalFilename());
		    	System.out.println(path);
		    	Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
		    }
 		  session.setAttribute("successMsg", "Admin Added  successfully");

	  }
	  else {
		  session.setAttribute("errormessage", "something went wrong");
	  }

	 
	 return "redirect:/admin/add-admin";
 }
 
 
 
 
 private UserDlt getLoggedInUserDetails(Principal p) {
		String email=p.getName();
		UserDlt userDlt=userservice.getUserByEmail(email);
		return userDlt;
	}

 
 
 @GetMapping("/admin-profile")
 public String profile() {
 	return  "/admin/admin_profile";
 }

 @PostMapping("/update-profile")
 public String updateProfile(@ModelAttribute UserDlt user,@RequestParam MultipartFile img,HttpSession session) {
 	 UserDlt updateuser=userservice.updateuserProfile(user, img);
 	if(!ObjectUtils.isEmpty(updateuser)) {
 		session.setAttribute("successMsg", " profile Status Updated");
 	} else {
 		session.setAttribute("errormessage", " profile status not updated");
 	}
 	return "redirect:/admin/admin-profile";
 }
 @PostMapping("/change-password")
 public String changePassword(@RequestParam String newPassword,@RequestParam String currentpassword,Principal p,HttpSession session) {
 	UserDlt loggeduser=getLoggedInUserDetails(p);
 	 boolean matches=passwordEncoder.matches(currentpassword, loggeduser.getPassword());
     if(matches) {
     	String encodepass=passwordEncoder.encode(newPassword);
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
 	return "redirect:/admin/admin-profile";
 }
 
 
	@GetMapping("/viewadmin")
	public String viewadmins(Model m, HttpSession session) {
	    List<UserDlt> admin = userservice.getAllAdmins("ROLE_ADMIN");

	    System.out.println("Fetched Users: " + admin); // Debugging line

	    m.addAttribute("users", admin);
	    return "/admin/viewadmin";
	}

	
	@GetMapping("/updateadminStatus")
	public String updateAdminAccountStatus(@RequestParam Boolean status,@RequestParam Integer id,HttpSession session) {
//		System.out.println("Received update request for user ID: " + id + ", Status: " + status);
		Boolean f=userservice.updateAccountStatus(id,status);

		if(f)
		{
			session.setAttribute("succMsg", "Account update");
		}
		else {
			session.setAttribute("errormessage", "Account  not update somthing went wrong");

		}
		return "redirect:/admin/viewadmin";
	}

	
}


