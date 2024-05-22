package com.example.securingweb.Configuraciones;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration 
public class MvcConfig implements WebMvcConfigurer 
{
	public void addViewControllers(ViewControllerRegistry registry) 
	{
		registry.addViewController("/").setViewName("mainPage");
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/error/403").setViewName("error/403");
		registry.addViewController("/error/404").setViewName("error/404");
	}
}