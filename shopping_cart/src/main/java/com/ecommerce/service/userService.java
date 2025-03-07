package com.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.model.UserDlt;

public interface userService   {
  public UserDlt saveUser(UserDlt user);
  public  UserDlt getUserByEmail(String email);
public List<UserDlt> getAllUsers(String role);
public Boolean updateAccountStatus(Integer id, Boolean status);
public void increaseFailedAttempt(UserDlt user);
public void UserAccountLock(UserDlt user);
public boolean unLockAccountTimeExpired(UserDlt user);
public void resetAttempt(int userId);
public void updateUserResetToken(String email, String resetToken);
public UserDlt getUserByToken(String token);
public UserDlt updateUser(UserDlt user);
 public UserDlt updateuserProfile(UserDlt user, MultipartFile img);
 public UserDlt saveAdmin(UserDlt user);
 public List<UserDlt> getAllAdmins(String role);
 public boolean existsEmail(String email);

}
