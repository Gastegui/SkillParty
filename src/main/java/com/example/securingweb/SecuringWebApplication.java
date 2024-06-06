package com.example.securingweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.ComponentScan;

/* Clase principal de la aplicación Spring Boot */
@SpringBootApplication
public class SecuringWebApplication 
{
	/* Método principal para ejecutar la aplicación Spring Boot */
	public static void main(String[] args) 
	{
		SpringApplication.run(SecuringWebApplication.class, args);
	}
}