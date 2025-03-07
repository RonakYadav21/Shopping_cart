package com.ecommerce.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.ecommerce.model.UserDlt;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.userService;
import com.ecommerce.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler{
     @Autowired
	private UserRepository userrepo;
     
     @Autowired
     private userService userservice;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
        String email=request.getParameter("username");
       UserDlt user=  userrepo.findByEmail(email);
       if(user!=null) {
       if(user.getIsEnable()) {
    	   if(user.getAccountNonLocked()) {
    		  if(user.getFailedAttempt()<AppConstant.ATTEMPT_TIME) {
    			  userservice.increaseFailedAttempt(user);
    		  }else {
    			  userservice.UserAccountLock(user);
    			  exception=new LockedException("your account is locked !! failed attempt 3");
    		  }
    	   }else {
    		   
    		   if(userservice.unLockAccountTimeExpired(user)) {
            	   exception =new LockedException("your account is unlocked !! please try ot login");
    		   }
    		   else {
    			   
    			   exception =new LockedException("your account is locked !! please  try after sometimes");
    		   }

    	   }
       }
       else {
    	   exception =new LockedException("your account is inactive");
       }
       }
       else {
    	   exception =new LockedException("Wrong User Id and Password");

       }
       
       //super.onAuthenticationFailure(...) calls the onAuthenticationFailure(...) method of SimpleUrlAuthenticationFailureHandler.
       //super is used To execute the default failure handling behavior of SimpleUrlAuthenticationFailureHandler.

       
       
       super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);//User submits login form (request contains form data).
		//If authentication fails:
		//request provides the username.
//The exception identifies the failure reason.

//response redirects the user to /signin?error.
	}
	

}
