package com.ecommerce.service.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	private ProductRepository productrepository; 
	
	@Override
	public Product saveProduct(Product product) {
				Product savedproduct=productrepository.save(product) ;
				return savedproduct;
	}


	@Override
	public Boolean existProduct(String title) {
		return productrepository.existsByTitle(title);
	}

	@Override
	public List<Product> getAllProduct() {
		return productrepository.findAll();
	
	}
	@Override
	public Boolean deleteProduct(int id) {
             Product product=productrepository.findById(id).orElse(null);
             if(!ObjectUtils.isEmpty(product)) {
            	 productrepository.delete(product);
	               return true;
                      } 
           return false; 
	}


	@Override
	public Product getProductById(int id) {
        Product product=productrepository.findById(id).orElse(null);
        
		return product;
	}


	@Override
	public List<Product> getAllActiveProduct(String category) {
		List<Product>product=null;
		if(ObjectUtils.isEmpty(category)) {
			 product=productrepository.findByIsActiveTrue()	;
		}
		else {
			product=productrepository.findByCategory(category);
		}
		return product;
	}


	@Override
	public List<Product> searchProduct(String ch) {
      return productrepository.findByTitleContainingIgnoreCaseOrCategoryIgnoreCase(ch, ch);
	}


	@Override
	public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize,String category) {
		Pageable pageable=PageRequest.of(pageNo,pageSize);
		Page<Product> pageproduct=null;
		if(ObjectUtils.isEmpty(category)) {
			pageproduct=productrepository.findByIsActiveTrue(pageable)	;
		}
		else {
			pageproduct=productrepository.findByCategory(pageable,category);
		}
              return pageproduct;
	}	
	public Page<Product> searchproductActiveProductPagination(Integer pageNo, Integer pageSize, String category,String ch){
		
		Page<Product> pageproduct=null;
		Pageable pageable=PageRequest.of(pageNo,pageSize);
	     pageproduct=  productrepository.findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch,pageable);

//		if(ObjectUtils.isEmpty(category)) {
//			pageproduct=productrepository.findByIsActiveTrue(pageable)	;
//		}
//		else {
//			pageproduct=productrepository.findByCategory(pageable,category);
//		}
              return pageproduct;

	}

}
