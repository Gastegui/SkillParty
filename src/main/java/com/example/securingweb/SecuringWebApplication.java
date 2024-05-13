package com.example.securingweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.securingweb.Configuraciones", "com.example.securingweb.Controladores", "com.example.securingweb.ORM"})
public class SecuringWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecuringWebApplication.class, args);
	}

}
