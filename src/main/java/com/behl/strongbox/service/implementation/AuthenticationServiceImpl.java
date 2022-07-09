package com.behl.strongbox.service.implementation;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.behl.strongbox.dto.RefreshTokenRequestDto;
import com.behl.strongbox.dto.TokenSuccessResponseDto;
import com.behl.strongbox.dto.UserAuthorizationRequestDto;
import com.behl.strongbox.repository.UserRepository;
import com.behl.strongbox.security.utility.JwtUtility;
import com.behl.strongbox.service.AuthenticationService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtility jwtUtility;

	@Override
	public TokenSuccessResponseDto login(@NonNull final UserAuthorizationRequestDto userAuthorizationRequestDto) {
		final var user = userRepository.findByUserName(userAuthorizationRequestDto.getUserName()).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login credentials provided"));

		if (!passwordEncoder.matches(userAuthorizationRequestDto.getPassword(), user.getPassword()))
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login credentials provided");

		final var accessToken = jwtUtility.generateAccessToken(user);
		final var refreshToken = jwtUtility.generateRefreshToken(user);
		final var accessTokenExpirationTimestamp = jwtUtility.extractExpirationTimestamp(accessToken);

		return TokenSuccessResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken)
				.expiresAt(accessTokenExpirationTimestamp).build();
	}

	@Override
	public TokenSuccessResponseDto refreshToken(@NonNull final RefreshTokenRequestDto refreshTokenRequestDto) {

		if (jwtUtility.isTokenExpired(refreshTokenRequestDto.getRefreshToken()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token expired");

		final UUID userId = jwtUtility.extractUserId(refreshTokenRequestDto.getRefreshToken());
		final var user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user-id provided"));

		final var accessToken = jwtUtility.generateAccessToken(user);
		final var accessTokenExpirationTimestamp = jwtUtility.extractExpirationTimestamp(accessToken);

		return TokenSuccessResponseDto.builder().accessToken(accessToken).expiresAt(accessTokenExpirationTimestamp)
				.build();
	}

}
