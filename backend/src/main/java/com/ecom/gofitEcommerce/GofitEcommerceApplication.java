package com.ecom.gofitEcommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GofitEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GofitEcommerceApplication.class, args);
	}

}
