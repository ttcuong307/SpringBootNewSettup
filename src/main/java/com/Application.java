package com;

import com.security.OAuth2.OAuth2Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OAuth2Properties.class)
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
