package com.springboot.contactmgm.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.contactmgm.entities.Contact;
import com.springboot.contactmgm.entities.User;
import com.springboot.contactmgm.helper.Message;
import com.springboot.contactmgm.repository.ContactRepository;
import com.springboot.contactmgm.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	ContactRepository contactRepository;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	// method for adding commen data
	@ModelAttribute
	public void addData(Model model, Principal principal) {
		// get username of logged in user
		String name = principal.getName();
		System.out.println("UserName " + name);
		// Get All data of loged in user
		User user = userRepository.getUserByUserName(name);
		System.out.println("User Data " + user);
		// send user data to screen
		model.addAttribute("userdata", user);
	}

	// dashboard
	@GetMapping("/dashboard")
	public String userDashBoard(Model model) {
		model.addAttribute("title", "Dashboard");
		return "user/dashboard";
	}

	// add contact
	@GetMapping("/addcontact")
	public String addCobtact(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "user/addcontact";
	}

	// save contacts to db
	@PostMapping("/savecontact")
	public String saveContact(@ModelAttribute @Valid Contact contact, BindingResult bindingResult,
			@RequestParam("image") MultipartFile file, Principal principal, HttpSession session) throws IOException {
		try {

			String name = principal.getName();
			User user = userRepository.getUserByUserName(name);
			/*
			 * if(3>2) { throw new Exception(); }
			 */
			// process to save the image
			if (file.isEmpty()) {
				// if file is empty display the message
				System.out.println("File Is Empty !");
				contact.setImage("contact.png");
			} else {

				// create random string
				char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
				StringBuilder sb = new StringBuilder(20);
				Random random = new Random();
				for (int j = 0; j < 20; j++) {
					char c = chars[random.nextInt(chars.length)];
					sb.append(c);
				}
				String savefile = sb.toString();
				System.out.println("random String:" + savefile);
				String saveImage = savefile + file.getOriginalFilename();
				System.out.println(saveImage);
				// +file.getOriginalFilename()
				// save file to folder
				contact.setImage(saveImage);
				File saveFile = new ClassPathResource("static/images").getFile();
				System.out.println("Save File = " + saveFile);

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + saveImage);
				System.out.println("Path = " + path);
				System.out.println("Absolute  Path = " + saveFile.getAbsolutePath());
				// System.out.println("File Seperator = "+File.separator);
				// System.out.println("Original Name = "+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}

			contact.setUser(user);
			user.getContacts().add(contact);
			userRepository.save(user);

			System.out.println("Data  " + contact);
			System.out.println("Addedd!!");
			// success message
			session.setAttribute("message", new Message("Data Added Successfully !!", "success"));
		} catch (Exception e) {
			System.out.println("Error  " + e.getMessage());
			e.printStackTrace();
			// error message
			session.setAttribute("message", new Message("Somthing Went Wrong, Try again   !!", "danger"));
		}
		return "user/addcontact";
	}

	// display all contacts
	// per page = 5[n]
	// current page = 0[page]
	@GetMapping("/showcontacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Contact Lists");
		// send list of contacts
		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);
		// currentPage-page
		// contact per page = 5
		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = contactRepository.findContactByUserId(user.getId(), pageable);
		model.addAttribute("contacts", contacts);
		// contact per page
		model.addAttribute("currentPage", page);
		// total pages
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "user/showcontacts";
	}

	// showing selected contact description/details
	@GetMapping("/contact/{cid}")
	public String showSingleContact(@PathVariable("cid") Integer cid, Model model, Principal principal) {
		System.out.println("Contact Id = " + cid);
		Optional<Contact> findById = contactRepository.findById(cid);
		Contact contact = findById.get();

		String username = principal.getName();
		User user = userRepository.getUserByUserName(username);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("title", contact.getName());
			model.addAttribute("singlecontact", contact);
		}
		return "user/singlecontact";
	}

	// delete contact
	@GetMapping("/delete/{cid}")
	@Transactional
	public String deleteContact(@PathVariable("cid") Integer cid, HttpSession session, Principal principal) {
		Optional<Contact> findById = contactRepository.findById(cid);
		Contact contact = findById.get();

		User user = userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		userRepository.save(user);

		/// contact.setUser(null);
		// contactRepository.delete(contact);
		session.setAttribute("message", new Message("Contact Deleted Successfully....", "success"));
//			if(cid == contact.getUser().getId()) {
//				contactRepository.delete(contact);
//				session.setAttribute("message", new Message("Contact Deleted Successfully....", "success"));
//				System.out.println("deleted id = "+cid);
//				}
		System.out.println("deleted id = " + cid);
		return "redirect:/user/showcontacts/0";
	}

	// update contact
	@PostMapping("/update/{cid}")
	public String updateContact(@PathVariable("cid") Integer cid, Model model) {
		model.addAttribute("title", "Update Contact");
		Contact contact = contactRepository.findById(cid).get();
		model.addAttribute("updatecontact", contact);
		return "user/updatecontact";
	}

	// update contact handler
	@PostMapping("/updatecontact")
	public String updateContact(@ModelAttribute @Valid Contact contact, BindingResult bindingResult,
			@RequestParam("image") MultipartFile file, Principal principal, HttpSession session) {
		try {
			// old contact details

			Contact oldcontactdetails = contactRepository.findById(contact.getCid()).get();

			if (!file.isEmpty()) {
				// image work

				// delete old pic
				File deleteFile = new ClassPathResource("static/images").getFile();
				File delete = new File(deleteFile, oldcontactdetails.getImage());
				delete.delete();
				System.out.println("deleted file " + deleteFile + "\n delete" + delete);
				// update image
				// create random string
				char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
				StringBuilder sb = new StringBuilder(20);
				Random random = new Random();
				for (int j = 0; j < 20; j++) {
					char c = chars[random.nextInt(chars.length)];
					sb.append(c);
				}
				String savefile = sb.toString();
				// System.out.println("random String:" + savefile);
				String saveImage = savefile + file.getOriginalFilename();
				// System.out.println(saveImage);
				// +file.getOriginalFilename()
				// save file to folder
				contact.setImage(saveImage);
				File saveFile = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + saveImage);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(saveImage);

			} else {
				contact.setImage(oldcontactdetails.getImage());
			}
			User user = userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			contactRepository.save(contact);
			session.setAttribute("message", new Message("Contact Updated Successfully", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/contact/" + contact.getCid();
	}

	// our profile
	@GetMapping("/profile")
	public String displayProfile(Model model) {
		model.addAttribute("title", "Profile");
		return "user/profile";
	}

	// Setting controller
	@GetMapping("/setting")
	public String openSetting() {
		return "user/setting";
	}

	// change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword,Principal principal,HttpSession session) {
		System.out.println("Old Pass = " + oldPassword);
		System.out.println("New Password = " + newPassword);
		String username = principal.getName();
		User currentUser = userRepository.getUserByUserName(username);
		System.out.println("Password = "+currentUser.getPassword());
		
		if(bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			// Change Password
			currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepository.save(currentUser);
			session.setAttribute("message", new Message("Your Password Change SuccessFullly   !!", "success"));
		}else {
			//Error
			session.setAttribute("message", new Message("Old Password Went Wrong, Try again   !!", "danger"));
			return "user/setting";
		}
		
		return "user/dashboard";
	}

}