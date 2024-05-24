package com.example.securingweb.Configuraciones;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/* Anotaciones para indicar que esta clase es una configuración de Spring Security y habilitar la seguridad web */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig 
{
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
	{
		http
			.authorizeHttpRequests((requests) -> requests
				//GENERALES
				.requestMatchers("/", "/home").permitAll() 
				.requestMatchers("/js/*", "/css/*", "/video/*", "/images/*").permitAll() 
				.requestMatchers("/login", "/createUser").permitAll() 
				.requestMatchers("/snake", "/juegos/snake.js", "/juegos/snake.css").permitAll() 
				.requestMatchers("/error", "/error/403", "error/404").permitAll() 
				//SERVICIOS
				.requestMatchers("/service", "/service/view", "service/list").permitAll()
				.requestMatchers("/service/create", "/service/createOption", "service/createSample", "service/publish").hasAnyAuthority("CREATE_SERVICE", "CREATE_ALL", "ADMIN")
				.requestMatchers("/service/delete", "/service/deleteOption", "service/deleteSample").hasAnyAuthority("CREATE_SERVICE", "CREATE_ALL", "ADMIN")
				.requestMatchers("/service/edit", "/service/editOption", "service/editSample", "service/editSamplePos").hasAnyAuthority("CREATE_SERVICE", "CREATE_ALL", "ADMIN")
				.requestMatchers("/service/rate", "/service/deleteRating", "service/buy").authenticated()
				//USUARIOS
				.requestMatchers("/user/addBalance").authenticated()
				.anyRequest().authenticated() //Esto tal vez habría que quitarlo
			)
			.formLogin((form) -> form
				.loginPage("/login").permitAll()
			)
			.logout((logout) -> logout.permitAll());

		return http.build();
	}

	/* Bean para proporcionar un codificador de contraseñas */
    @Bean
    public PasswordEncoder passwordEncoder() 
	{
		return new BCryptPasswordEncoder();
    }

	/* Bean para proporcionar un administrador de autenticación */
	@Bean
	public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,PasswordEncoder passwordEncoder) 
	{
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);
		return new ProviderManager(authenticationProvider);
	}
}