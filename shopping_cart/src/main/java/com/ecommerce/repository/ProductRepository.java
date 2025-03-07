package com.ecommerce.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.model.Product;

public interface ProductRepository extends JpaRepository<Product,Integer> {

	public Boolean existsByTitle(String title);

	public List<Product> findByIsActiveTrue();

	public List<Product> findByCategory(String category);
	
     List<Product> findByTitleContainingIgnoreCaseOrCategoryIgnoreCase(String ch,String ch2);

	public Page<Product> findByIsActiveTrue(Pageable pageable);

	public Page<Product> findByCategory(Pageable pageable, String category);

	public Page<Product> findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2, Pageable pageable);


}
