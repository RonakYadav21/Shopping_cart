package com.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {
	@Autowired
	private AuthenticationSuccessHandler authenticationsuccesshandler;
	
	@Autowired
	@Lazy
	private AuthFailureHandlerImpl authenticationfailurehandler;
	@Bean
	public PasswordEncoder passwordEncoder() {
		  return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsservice() {

		return new UserDetailsServiceImpl();
}
	
	//DaoAuthenticationProvider to check whether the provided user pass  by user is correct or not
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authentictionprovider = new DaoAuthenticationProvider();
		authentictionprovider.setUserDetailsService(userDetailsservice());
		authentictionprovider.setPasswordEncoder(passwordEncoder());
		return authentictionprovider;
		
	}
	@Bean
	public SecurityFilterChain filterchain(HttpSecurity http) throws Exception {
		//defaultSuccessUrl("/") → After a successful login, the user is redirected to the home page (/).
		//loginProcessingUrl("/login") → When a user submits the login form, it should be processed at /login (Spring Security handles authentication here).

		http.csrf(csrf->csrf.disable()).cors(cors->cors.disable()).authorizeHttpRequests(req->req.requestMatchers("/user/**") //The custom login page is at /signin (instead of the default Spring Security login page)
				.hasRole("USER").requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/**").permitAll()).formLogin(form->form.loginPage("/signin").loginProcessingUrl("/login")//.defaultSuccessUrl("/")) //login is the form action
		 .failureHandler(authenticationfailurehandler).successHandler(authenticationsuccesshandler) //Specifies a custom authentication failure handler and success Handler.

		)
		.logout(logout->logout.permitAll());
		return http.build();
	}
}
