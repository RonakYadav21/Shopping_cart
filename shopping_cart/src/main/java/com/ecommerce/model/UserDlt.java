package com.ecommerce.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public class UserDlt {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column
private Integer id;
private String name;
private String mobileNumber;
private String email;
private String address;
private String city;
private String state;
private String pincode;
private String password;
private String profileImage;
private String role;
private Boolean isEnable;
private Boolean accountNonLocked;
private Integer failedAttempt;
private Date lockTime;
private String resetToken;
}
