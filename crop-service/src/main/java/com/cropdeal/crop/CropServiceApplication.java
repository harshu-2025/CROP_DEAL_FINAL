package com.cropdeal.crop;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients
public class CropServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CropServiceApplication.class, args);
	}

}
