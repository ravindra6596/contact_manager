package com.springboot.contactmgm.controller;

import java.security.Principal;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.contactmgm.entities.User;
import com.springboot.contactmgm.helper.Message;
import com.springboot.contactmgm.repository.UserRepository;
import com.springboot.contactmgm.service.Email;


@Controller
public class ForgotController {
	@Autowired
	private Email emailService; 
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	//email id form open handler
	@GetMapping("/forgot")
	public String openEmailForm() {
		return "forgot_email_handler";
	}
	// send-OTP handler
	@PostMapping("/send-OTP")
	public String sendOTP(@RequestParam("email")String email,HttpSession session) {
		System.out.println("Your Email = "+email);
		
		//generating OTP of 4 didgit
		Random random = new Random();
		int OTP = random.nextInt(999999);
		System.out.println("OTP = "+OTP);
		//right code for send  OTP to email
		String subject = "OTP From Contact Manager";
		String message = "<div style='border:1px solid #e2e2e2; padding:20px;'>"
				+ "<h3>"
				+ "Your OTP For Contact Manager Is  "
				+"<br>"
				+OTP
				+ "</h3>"
				+ "</div>";
		String to = email;
		boolean flag = emailService.sendMessage(subject, message, to);
		if (flag) {
			session.setAttribute("myotp",OTP);
			session.setAttribute("email",email);
			return "verify_OTP";
		} else {
			session.setAttribute("message", new Message("Email Id Wrong, Try again   !!", "danger"));
			return "forgot_email_handler";
		}
		
	}
	
	//verify OTP
	@PostMapping("/verify-OTP")
	public String verifyOTP(@RequestParam("OTP") int otp,HttpSession session) {
		int myotp = (int)session.getAttribute("myotp");
		String email = (String)session.getAttribute("email");
		if(myotp==otp) {
			//password change form
			User user = userRepository.getUserByUserName(email);
			System.out.println("INSIDE IF Wrong Email Address"+user);
			if(user == null) {
				//send error message
				System.out.println("Wrong Email Address"+user);
				session.setAttribute("message", new Message("User Does not exist with this email , Try again   !!", "danger"));
				return "forgot_email_handler";
			}
			else {
				//send change password form
				
			}
			return "changepassword";
		}else {
			session.setAttribute("message", new Message(" Wrong OTP, Try again   !!", "danger"));
			return "verify_OTP";
		}
	}
	
	//change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session) {
		String email = (String)session.getAttribute("email");
		User user = userRepository.getUserByUserName(email);
		user.setPassword(bCryptPasswordEncoder.encode(newpassword));
		User save = userRepository.save(user);
		//return "redirect:signin?change=Password changed successfully!!";
		return "redirect:signin?change";
	}
}
