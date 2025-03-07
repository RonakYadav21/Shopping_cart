$(function () {
  var $userRegister = $("#userRegister");

  // Custom method to prevent spaces
  jQuery.validator.addMethod("space", function (value, element) {
    return !/\s/.test(value);
  }, "Spaces are not allowed");

  // Custom method to allow only numbers
  jQuery.validator.addMethod("numericOnly", function (value, element) {
    return /^[0-9]+$/.test(value);
  }, "Only numeric values are allowed");

  // Custom method to allow all characters (for address)
  jQuery.validator.addMethod("all", function (value, element) {
    return /^[A-Za-z0-9\s,.'-]+$/.test(value);
  }, "Invalid characters in address");

  // Custom method to allow only letters
  jQuery.validator.addMethod("lettersonly", function (value, element) {
    return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
  }, "Invalid name");

  $userRegister.validate({
    rules: {
      name: {
        required: true,
        lettersonly: true,
      },
      email: {
        required: true,
        space: true,
        email: true,
      },
      mobileNumber: {
        required: true,
        space: true,
        numericOnly: true,
        minlength: 10,
        maxlength: 12,
      },
      password: {
        required: true,
        space: true,
      },
      confirmpassword: {
        required: true,
        space: true,
        equalTo: "#pass",
      },
      address: {
        required: true,
        all: true,
      },
      city: {
        required: true,
        space: true,
      },
      state: {
        required: true,
      },
      pincode: {
        required: true,
        space: true,
        numericOnly: true,
      },
      img: {
        required: true,
      },
    },
    messages: {
      name: {
        required: "Name is required",
        lettersonly: "Invalid name",
      },
      email: {
        required: "Email is required",
        space: "Spaces are not allowed",
        email: "Invalid email format",
      },
      mobileNumber: {
        required: "Mobile number is required",
        space: "Spaces are not allowed",
        numericOnly: "Invalid mobile number",
        minlength: "Minimum 10 digits required",
        maxlength: "Maximum 12 digits allowed",
      },
      password: {
        required: "Password is required",
        space: "Spaces are not allowed",
      },
      confirmpassword: {
        required: "Confirm password is required",
        space: "Spaces are not allowed",
        equalTo: "Password mismatch",
      },
      address: {
        required: "Address is required",
        all: "Invalid characters in address",
      },
      city: {
        required: "City is required",
        space: "Spaces are not allowed",
      },
      state: {
        required: "State is required",
      },
      pincode: {
        required: "Pincode is required",
        space: "Spaces are not allowed",
        numericOnly: "Invalid pincode",
      },
      img: {
        required: "Profile image is required",
      },
    },
  });
  
  // Reset Password Validation

  var $resetPassword=$("#resetPassword");

  $resetPassword.validate({
  		
  		rules:{
  			Password: {
  				required: true,
  				space: true

  			},
  			confirmPassword: {
  				required: true,
  				space: true,
  				equalTo: '#pass'

  			}
  		},
  		messages:{
  		   Password: {
  				required: 'password must be required',
  				space: 'space not allowed'

  			},
  			confirmPassword: {
  				required: 'confirm password must be required',
  				space: 'space not allowed',
  				equalTo: 'password mismatch'

  			}
  		}	
  })

  // Orders Validation

  var $orders=$("#orders");

  $orders.validate({
  		rules:{
  			firstName:{
  				required:true,
  				lettersonly:true
  			},
  			lastName:{
  				required:true,
  				lettersonly:true
  			}
  			,
  			email: {
  				required: true,
  				space: true,
  				email: true
  			},
  			mobileNo: {
  				required: true,
  				space: true,
  				numericOnly: true,
  				minlength: 10,
  				maxlength: 12

  			},
  			address: {
  				required: true,
  				all: true

  			},

  			city: {
  				required: true,
  				space: true

  			},
  			state: {
  				required: true,


  			},
  			pincode: {
  				required: true,
  				space: true,
  				numericOnly: true

  			},
  			paymentType:{
  			required: true
  			}
  		},
  		messages:{
  			firstName:{
  				required:'first required',
  				lettersonly:'invalid name'
  			},
  			lastName:{
  				required:'last name required',
  				lettersonly:'invalid name'
  			},
  			email: {
  				required: 'email name must be required',
  				space: 'space not allowed',
  				email: 'Invalid email'
  			},
  			mobileNo: {
  				required: 'mob no must be required',
  				space: 'space not allowed',
  				numericOnly: 'invalid mob no',
  				minlength: 'min 10 digit',
  				maxlength: 'max 12 digit'
  			}
  		   ,
  			address: {
  				required: 'address must be required',
  				all: 'invalid'

  			},

  			city: {
  				required: 'city must be required',
  				space: 'space not allowed'

  			},
  			state: {
  				required: 'state must be required',
  				space: 'space not allowed'

  			},
  			pincode: {
  				required: 'pincode must be required',
  				space: 'space not allowed',
  				numericOnly: 'invalid pincode'

  			},
  			paymentType:{
  			required: 'select payment type'
  			}
  		}	
  })

});
