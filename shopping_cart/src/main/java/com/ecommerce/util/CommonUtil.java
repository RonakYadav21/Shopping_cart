package com.ecommerce.util;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.ecommerce.model.ProductOrder;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {
	@Autowired
	private  JavaMailSender mailSender; //interface
public   Boolean SendMail(String url, String recipentemail) throws UnsupportedEncodingException, MessagingException {
	MimeMessage message=mailSender.createMimeMessage();
	MimeMessageHelper helper=new MimeMessageHelper(message);
	helper.setFrom("ronakyadav9977@gmail.com","shopping app");
	helper.setTo(recipentemail);
	String content="<p>hellow,</p>"+"<p>You Have request to reset your password</p>"+"<P>click the link to change your password:</p> "+"<p><a href=\""+url+"\">change my password</a></p>";
	helper.setSubject("Password Reset");
	helper.setText(content,true);//The second parameter true enables HTML content in the email.

	mailSender.send(message);
	return true;
}

public static String generateUrl(HttpServletRequest request) {
    String siteUrl = request.getRequestURL().toString(); // Get full URL (http://localhost:8080/forgot_password)
    return siteUrl.replace(request.getRequestURI(), ""); // Remove path, keep domain (http://localhost:8080)
}


String msg="<p> Hello[[name]]</p><p> Thankyou  your order <b>[[orderStatus]]</b>. </p>"
+"<p><b>Product Details :</b></p>"
+"<p>Name : [[ProductName]]</p>"
+"<p>category : [[Category]]</p>"
+"<p>Quantity : [[quantity]]</p>"
+"<p>Price : [[price]] </p>"
+"<p>Payment Type :[[paymenttype]] </p>"
;
public Boolean SendMailForProductOrder(ProductOrder order,String  status) throws UnsupportedEncodingException, MessagingException {
	
	MimeMessage message=mailSender.createMimeMessage();
	MimeMessageHelper helper=new MimeMessageHelper(message);
	helper.setFrom("ronakyadav9977@gmail.com","shopping app");
	helper.setTo(order.getOrderAddress().getEmail());
	helper.setSubject("Product  Order Status ");
	msg=msg.replace("[[orderStatus]]",status);
	msg=msg.replace("[[name]]", order.getOrderAddress().getFirstName() );
	msg=msg.replace("[[ProductName]]", order.getProduct().getTitle() );
	msg=msg.replace(" [[Category]]", order.getProduct().getCategory() );
	msg=msg.replace(" [[quantity]]", order.getQuatity().toString());
	msg=msg.replace(" [[price]]", order.getPrice().toString());
	msg=msg.replace(" [[paymenttype]]", order.getPaymentType());
	helper.setText(msg,true);//The second parameter true enables HTML content in the email.

	mailSender.send(message);
	return true;
}
}
