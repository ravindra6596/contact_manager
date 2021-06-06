package com.springboot.contactmgm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.contactmgm.entities.Contact;
import com.springboot.contactmgm.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	@Query(value = "select * from contact c where c.user_id =:userId ",nativeQuery = true)
	//currentPage-page
	//contact per page = 5
	public Page<Contact> findContactByUserId(@Param("userId") int userId,Pageable pageable);
	
	//Serch bar
	public List<Contact> findByNameContainingAndUser(String name,User user);
}
