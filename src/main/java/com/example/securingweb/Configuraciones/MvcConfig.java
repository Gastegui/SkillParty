package com.example.securingweb.Configuraciones;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration 
public class MvcConfig implements WebMvcConfigurer 
{
	public void addViewControllers(ViewControllerRegistry registry) 
	{
		registry.addViewController("/").setViewName("mainPage");
		registry.addViewController("/user/login").setViewName("user/login");
		registry.addViewController("/login").setViewName("user/login");
		registry.addViewController("/error/403").setViewName("error/403");
		registry.addViewController("/error/404").setViewName("error/404");
	}

	@Bean
	public LocaleResolver localeResolver()
	{
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(new Locale("es"));
		return slr;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor()
	{
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	@Override
	public void addInterceptors (InterceptorRegistry intercepto)
	{
		intercepto.addInterceptor(localeChangeInterceptor());
	}

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.home") + "/ficheros/");
    }
}