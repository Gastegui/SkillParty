package com.example.securingweb;

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

@Configuration
@EnableWebSecurity
public class WebSecurityConfig 
{
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
	{
		http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/", "/home", "/createUser").permitAll()
				.requestMatchers("/login").permitAll()
				.requestMatchers("/snake", "/juegos/snake.js", "/juegos/snake.css").permitAll()
				.requestMatchers("/tetris", "/juegos/tetris.js", "/juegos/tetris.css").permitAll()
				.requestMatchers("/prueba").hasAuthority("PRUEBA")
				.requestMatchers("/hello").hasAuthority("USER")
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				.loginPage("/login").permitAll()
			)
			.logout((logout) -> logout.permitAll());

		return http.build();
	}

    @Bean
    public PasswordEncoder passwordEncoder() 
	{
		return new BCryptPasswordEncoder();
    }

	@Bean
	public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,PasswordEncoder passwordEncoder) 
	{
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);
		return new ProviderManager(authenticationProvider);
	}
	
}