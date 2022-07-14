package com.behl.strongbox.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.behl.strongbox.security.utility.JwtUtility;
import com.behl.strongbox.security.utility.LoggedInUserDetailProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoggedInUserDetailStorageFilter extends OncePerRequestFilter {

	private final JwtUtility jwtUtility;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException {
		final var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			final var authorizationHeader = request.getHeader("Authorization");
			final var token = authorizationHeader.substring("Bearer ".length());
			final var userId = jwtUtility.extractUserId(token);

			LoggedInUserDetailProvider.setUserId(userId);
		}
		filterChain.doFilter(request, response);
	}

}