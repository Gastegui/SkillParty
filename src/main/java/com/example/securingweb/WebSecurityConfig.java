package com.example.securingweb;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import com.example.securingweb.ORM.Usuario;
import com.example.securingweb.ORM.UsuarioService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/", "/home", "/createUser").permitAll()
				.requestMatchers("/login").permitAll()
				.requestMatchers("/snake", "/juegos/snake.js", "/juegos/snake.css").permitAll()
				.requestMatchers("/tetris", "/juegos/tetris.js", "/juegos/tetris.css").permitAll()
				.requestMatchers("/prueba").hasRole("PRUEBA")
				.requestMatchers("/hello").hasRole("USER")
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				.loginPage("/login").permitAll()
			)
			.logout((logout) -> logout.permitAll());

		return http.build();
	}

	@Bean
	public InMemoryUserDetailsManager inMemoryUserDetailsManager(UsuarioService us) {
		InMemoryUserDetailsManager ret = new InMemoryUserDetailsManager();

		List<Usuario> usuarios = us.obtenerTodosLosUsuarios();
		for(Usuario u : usuarios)
		{
			System.out.println("Cargando usuario: " + u.getNombre());
			UserDetails user = User.withDefaultPasswordEncoder()
								.username(u.getNombre())
								.password(u.getContraseña())
								.roles(u.getRol())
								.build();		

			ret.createUser(user);
		}
		return ret;
	}
}