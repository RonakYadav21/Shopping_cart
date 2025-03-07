package com.ecommerce.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.model.UserDlt;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.userService;
import com.ecommerce.util.AppConstant;

@Service
public class UserDetailsImpl implements userService {
@Autowired
private UserRepository userrepository;

@Autowired
private PasswordEncoder passwordEncoder;
	@Override
	public UserDlt saveUser(UserDlt user) {
		user.setRole("ROLE_USER");
		user.setIsEnable(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		String encodePassword =passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		  UserDlt saveuser=userrepository.save(user)	;
		return saveuser;
	}
	@Override
	public UserDlt getUserByEmail(String email) {
		// TODO Auto-generated method stub
		return userrepository.findByEmail(email);
	}
	@Override
	public List<UserDlt> getAllUsers(String role) {
		  List<UserDlt> user=userrepository.findByRole(role);
		return user;
	}
	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {
		 Optional<UserDlt>user=userrepository.findById(id);
		 if(user.isPresent()) {
			 UserDlt userdlt=user.get();
			 userdlt.setIsEnable(status);
			 userrepository.save(userdlt);
			 return true;
		 }
		return false;
	}
	@Override
	public void increaseFailedAttempt(UserDlt user) {
        int attempt=user.getFailedAttempt()+1;
        user.setFailedAttempt(attempt);
        userrepository.save(user);
		
	}
	@Override
	public void UserAccountLock(UserDlt user) {
        user.setAccountNonLocked(false);
//        user.setLockTime(new Date(0));
        user.setLockTime(new Date(System.currentTimeMillis()));

        userrepository.save(user);
	}
	@Override
	public boolean unLockAccountTimeExpired(UserDlt user) {
		  long locktime=user.getLockTime().getTime();
		  long unlocktime=locktime+AppConstant.UNLOCK_DURATION_TIME;
		long currentTime=  System.currentTimeMillis();
		if(unlocktime<currentTime) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
	        userrepository.save(user);
	        return true;
		}
return false;
	}
	@Override
	public void resetAttempt(int userId) {
		// TODO Auto-generated method stub
		
	}
	public void updateUserResetToken(String email, String resetToken) {
UserDlt findbyemail=userrepository.findByEmail(email)	;
findbyemail.setResetToken(resetToken);
userrepository.save(findbyemail);
	}
	@Override
	public UserDlt getUserByToken(String token) {
		return userrepository.findByresetToken(token);
			}
	@Override
	public UserDlt updateUser(UserDlt user) {
		return userrepository.save(user);
	}
	@Override
	public UserDlt updateuserProfile(UserDlt user ,MultipartFile img) {
UserDlt dbuser=	userrepository.findById(user.getId()).get();
if(!img.isEmpty()) {
	dbuser.setProfileImage(img.getOriginalFilename());
}

if(!ObjectUtils.isEmpty(dbuser)) {
	dbuser.setName(user.getName());
	dbuser.setCity(user.getCity());
	dbuser.setAddress(user.getAddress());
	dbuser.setMobileNumber(user.getMobileNumber());
	  dbuser=userrepository.save(dbuser);
}

try {
	
if(!img.isEmpty()) {
	File savefile=new ClassPathResource("static/image").getFile();
	Path path=Paths.get(savefile.getAbsolutePath()+File.separator+"profile_img"+File.separator+img.getOriginalFilename());
	Files.copy(img.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
}
}catch(Exception e) {
	e.printStackTrace();
}
return dbuser;
	}
	@Override
	public UserDlt saveAdmin(UserDlt admin) {
		admin.setRole("ROLE_ADMIN");
		admin.setIsEnable(true);
		admin.setAccountNonLocked(true);
		admin.setFailedAttempt(0);
		String encodePassword =passwordEncoder.encode(admin.getPassword());
		admin.setPassword(encodePassword);
		  UserDlt saveadmin=userrepository.save(admin)	;
		return saveadmin;
	}

	
	@Override
	public List<UserDlt> getAllAdmins(String role) {
		  List<UserDlt> admin=userrepository.findByRole(role);
		return admin;
	}
	@Override
	public boolean existsEmail(String email) {
		  return userrepository.existsByEmail(email);
		
	}

	
	
}
