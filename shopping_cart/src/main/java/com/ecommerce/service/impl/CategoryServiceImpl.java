package com.ecommerce.service.impl;


import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryrepository;
	@Override
	public Category saveCategory(Category category) {
		if(categoryrepository.existsByName(category.getName())) {
			return null;
		}
		return categoryrepository.save(category);
	}

	@Override
	public List<Category> getAllCategory() {
		return categoryrepository.findAll();
	}

	@Override
	public Boolean existCategory(String name) {
		return categoryrepository.existsByName(name) ;
	}

	@Override
	public Boolean deleteCategory(int id) {
		 Category category=categoryrepository.findById(id).orElse(null);
		if(!ObjectUtils.isEmpty(category)) {
			categoryrepository.delete(category);
			return true;
		}
		return false;
	}

	@Override
	public Category getCategoryById(int id) {
		 Category category=categoryrepository.findById(id).orElse(null);
		return category;
	}

	@Override
	public List<Category> getAllActive() {
		 List<Category> category=categoryrepository.findByIsActiveTrue();
		return category;
	}

	@Override
	public Page<Category> getAllCategoryPagination(Integer pageNo, Integer PageSize) {
		Pageable pageable=PageRequest.of(pageNo, PageSize); //Pageable is an interface used to pass pagination information to Spring Data JPA repository methods.
       return categoryrepository.findAll(pageable)	;

	}


}
