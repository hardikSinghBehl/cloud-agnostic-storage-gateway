package com.behl.strongbox.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.behl.strongbox.dto.RefreshTokenRequestDto;
import com.behl.strongbox.dto.TokenSuccessResponseDto;
import com.behl.strongbox.dto.UserAuthorizationRequestDto;
import com.behl.strongbox.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Returns JWT tokens on successfull authentication", description = "Returns Access-token and Refresh-token on successfull authentication which provides access to protected endpoints")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Authentication successfull"),
			@ApiResponse(responseCode = "401", description = "Bad credentials provided. Failed to authenticate user") })
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<TokenSuccessResponseDto> authenticate(
			@Valid @RequestBody(required = true) final UserAuthorizationRequestDto userAuthorizationRequestDto) {
		return ResponseEntity.ok(authenticationService.login(userAuthorizationRequestDto));
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Refreshes Access-Token for an user-account", description = "Provides a new Access-token against the user for which the non expired refresh-token is provided")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Access-token refreshed"),
			@ApiResponse(responseCode = "403", description = "Refresh token has expired. Failed to refresh access token") })
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<TokenSuccessResponseDto> refreshToken(
			@Valid @RequestBody(required = true) final RefreshTokenRequestDto refreshTokenRequestDto) {
		return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequestDto));
	}
}
