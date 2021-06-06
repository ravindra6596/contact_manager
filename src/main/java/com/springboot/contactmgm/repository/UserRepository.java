package com.springboot.contactmgm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.contactmgm.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	@Query(value = "SELECT * FROM user u WHERE u.email = :email ",nativeQuery = true)	
	public User getUserByUserName(@Param("email") String email);
}
