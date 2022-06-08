package com.Aise.Server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.Aise.Server.security.JWTAuthorizationFilter;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@EnableWebSecurity
	@Configuration
	class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable()
				.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/").permitAll()
				.antMatchers(HttpMethod.GET, "/login").permitAll()
				.antMatchers(HttpMethod.GET, "/users").permitAll()
				.antMatchers(HttpMethod.GET, "/groups").permitAll()
				.antMatchers(HttpMethod.GET, "/courses").permitAll()
				.antMatchers(HttpMethod.GET, "/tasks").permitAll()
				.antMatchers(HttpMethod.GET, "/lessons").permitAll()
				.antMatchers(HttpMethod.GET, "/tasks/grade").permitAll()
				.antMatchers(HttpMethod.GET, "/rooms").permitAll()
				.antMatchers(HttpMethod.POST, "/users").permitAll()
				.antMatchers(HttpMethod.POST, "/groups").permitAll()
				.antMatchers(HttpMethod.POST, "/groups/user").permitAll()
				.antMatchers(HttpMethod.POST, "/courses").permitAll()
				.antMatchers(HttpMethod.POST, "/tasks").permitAll()
				.antMatchers(HttpMethod.POST, "/lessons").permitAll()
				.antMatchers(HttpMethod.PUT, "/tasks/grade").permitAll()
				.antMatchers(HttpMethod.PUT, "/tasks").permitAll()
				.antMatchers(HttpMethod.DELETE, "/tasks").permitAll()
				.antMatchers(HttpMethod.DELETE, "/lessons").permitAll()
				.anyRequest().authenticated();
		}
	}
}
