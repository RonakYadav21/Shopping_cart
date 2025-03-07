package com.ecommerce.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ecommerce.model.Product;

public interface ProductService {
   public Product saveProduct(Product product);
//   public Product findById(int id);
 
   public Boolean existProduct(String title);
public List<Product> getAllProduct();

public Boolean deleteProduct(int id);

public Product getProductById(int id);

public List<Product> getAllActiveProduct(String category);
public List<Product> searchProduct(String ch);
public Page<Product> getAllActiveProductPagination(Integer pageNo,Integer pagesize,String category);
public Page<Product> searchproductActiveProductPagination(Integer pageNo, Integer pageSize, String category,String ch);

}
