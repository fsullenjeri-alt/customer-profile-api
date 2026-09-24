package com.example.customerprofile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CustomerProfileApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerProfileApiApplication.class, args);
	}

}
