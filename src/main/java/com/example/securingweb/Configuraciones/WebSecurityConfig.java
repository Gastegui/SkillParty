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
				.requestMatchers("/snake", "/juegos/snake.js", "/juegos/snake.css").permitAll() 
				.requestMatchers("/error", "/error/403", "/error/404").permitAll() 
				.requestMatchers("/uploads/*/services/**", "/uploads/*/courses/**", "/uploads/*/profile.*").permitAll()
				
				//SERVICIOS
				.requestMatchers("/service", "/service/view", "/service/list").permitAll()
				.requestMatchers("/service/create", "/service/createOption", "/service/createSample", "/service/publish").hasAnyAuthority("CREATE_SERVICE", "CREATE_ALL", "ADMIN")
				.requestMatchers("/service/delete", "/service/deleteOption", "/service/deleteSample").hasAnyAuthority("CREATE_SERVICE", "CREATE_ALL", "ADMIN")
				.requestMatchers("/service/edit", "/service/editOption", "/service/editSample", "/service/editSamplePos").hasAnyAuthority("CREATE_SERVICE", "CREATE_ALL", "ADMIN")
				.requestMatchers("/service/rate", "/service/deleteRating", "/service/buy", "/service/finish").authenticated()
				
				//CURSOS
				.requestMatchers("/course", "/course/view", "/course/list").permitAll()
				.requestMatchers("/course/create", "/course/createElement", "/course/publish").hasAnyAuthority("CREATE_COURSE", "CREATE_ALL", "ADMIN")
				.requestMatchers("/course/delete", "/course/deleteElement").hasAnyAuthority("CREATE_COURSE", "CREATE_ALL", "ADMIN")
				.requestMatchers("/course/edit", "/course/editElement").hasAnyAuthority("CREATE_COURSE", "CREATE_ALL", "ADMIN")
				.requestMatchers("/course/rate", "/course/deleteRating", "/course/buy", "/course/content", "/course/finish").authenticated()
				
				//USUARIOS
				.requestMatchers("/login", "/user/login", "/user/create").permitAll() 
				.requestMatchers("/user/addBalance", "/user/edit", "user/panel", "/user/bought", "/user/becomePro").authenticated()
				.requestMatchers("/user/pending", "/user/services").hasAnyAuthority("CREATE_SERVICE", "CREATE_ALL", "ADMIN")
				.requestMatchers("/user/courses").hasAnyAuthority("CREATE_COURSE", "CREATE_ALL", "ADMIN")
				.requestMatchers("/user/published", "/user/claim").hasAnyAuthority("CREATE_SERVICE", "CREATE_COURSE", "CREATE_ALL", "ADMIN")
				.requestMatchers("/user/seeServices", "/user/seeCourses").permitAll()
				.requestMatchers("/user/verify").hasAnyAuthority("ADMIN")
				
				//CHAT
				.requestMatchers("/chat", "/send").authenticated()
				
				//SOCKET
				.requestMatchers("/websocket-endpoint").permitAll()
				
				.anyRequest().authenticated() //Esto tal vez habría que quitarlo
			)
			.formLogin((form) -> form
				.loginPage("/user/login")
				.defaultSuccessUrl("/?message=logged")
				.failureUrl("/user/login?message=invalidCombination")
				.permitAll()
			)
			.logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?message=logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }
}
