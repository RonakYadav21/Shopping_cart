package com.ecommerce.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;

public interface CategoryService {
 public Category saveCategory(Category category);
 public Boolean existCategory(String name);
 public List<Category> getAllCategory();
public Boolean deleteCategory(int id);
public Category getCategoryById(int id);
public List<Category> getAllActive();
public Page<Category> getAllCategoryPagination(Integer pageNo,Integer PageSize);
}
