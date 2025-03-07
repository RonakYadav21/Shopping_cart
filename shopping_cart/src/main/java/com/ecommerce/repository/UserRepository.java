package com.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.model.UserDlt;
import java.util.List;


public interface UserRepository extends JpaRepository<UserDlt,Integer>{
	public UserDlt findByEmail(String email);
	public List<UserDlt> findByRole(String role);
	public UserDlt findByresetToken(String token);
	public Boolean existsByEmail(String email);
}
