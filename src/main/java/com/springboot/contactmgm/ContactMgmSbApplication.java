package com.springboot.contactmgm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class ContactMgmSbApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContactMgmSbApplication.class, args);
	}

}
