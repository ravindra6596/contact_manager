package com.springboot.contactmgm.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.contactmgm.entities.User;
import com.springboot.contactmgm.helper.Message;
import com.springboot.contactmgm.repository.UserRepository;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String homePage(Model model) {
		model.addAttribute("title", "Contact Management");
		return "home";
	}

	@GetMapping("/signup")
	public String signUpPage(Model model) {
		model.addAttribute("user", new User());
		return "signup";
	}

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		try {
			if(!agreement) {
				System.out.println("Not Accept Agreement");
				throw new Exception("Not Accept Agreement");
			}
			if(result.hasErrors())
			{			model.addAttribute("user",user);
				return "signup";
			}
			 
			
			user.setRole("USER");;
			user.setActive(true);
			user.setProfile("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			User save = userRepository.save(user);
			model.addAttribute("user", new User());
			session.setAttribute("message",new Message("Successfully Registered!!", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message",new Message("Something Went Wrong!!"+e.getMessage(), "alert-danger"));
			return "signup";
		}
		
	}

	@GetMapping("/about")
	public String aboutPage(Model model) {
		return "about";
	}

	@GetMapping("/signin")
	public String loginPage(Model model) {
		return "index";
	}
}
