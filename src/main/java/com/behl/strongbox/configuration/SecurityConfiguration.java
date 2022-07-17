package com.behl.strongbox.configuration;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.behl.strongbox.security.CustomUserDetailService;
import com.behl.strongbox.security.constant.ApiPathExclusion;
import com.behl.strongbox.security.filter.JwtAuthenticationFilter;
import com.behl.strongbox.security.filter.LoggedInUserDetailStorageFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final CustomUserDetailService customUserDetialService;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final LoggedInUserDetailStorageFilter loggedInUserDetailStorageFilter;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserDetialService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().exceptionHandling().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers(HttpMethod.GET,
						List.of(ApiPathExclusion.GetApiPathExclusion.values()).stream()
								.map(apiPath -> apiPath.getPath()).toArray(String[]::new))
				.permitAll()
				.antMatchers(HttpMethod.POST,
						List.of(ApiPathExclusion.PostApiPathExclusion.values()).stream()
								.map(apiPath -> apiPath.getPath()).toArray(String[]::new))
				.permitAll()
				.antMatchers(HttpMethod.PUT,
						List.of(ApiPathExclusion.PutApiPathExclusion.values()).stream()
								.map(apiPath -> apiPath.getPath()).toArray(String[]::new))
				.permitAll().anyRequest().authenticated().and()
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterAfter(loggedInUserDetailStorageFilter, JwtAuthenticationFilter.class);

	}

}