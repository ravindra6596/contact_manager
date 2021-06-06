package com.springboot.contactmgm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.springboot.contactmgm.entities.User;
import com.springboot.contactmgm.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService{
	@Autowired
	private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.getUserByUserName(username);
		
		if (user ==null) {
			throw new UsernameNotFoundException("Could Not Found User");
		}
		CustomUserDetails userDetails = new CustomUserDetails(user);
		return userDetails ;
	}

}
