package com.behl.strongbox.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.behl.strongbox.dto.UserCreationRequestDto;
import com.behl.strongbox.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Creates a user account", description = "Registers a unique user-acccount record in the system corresponding to the provided information. This account may represent an application that wish to consume the exposed APIs")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "User account created successfully"),
			@ApiResponse(responseCode = "409", description = "User account with provided userName already exists") })
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<HttpStatus> userCreationHandler(
			@Valid @RequestBody(required = true) final UserCreationRequestDto userCreationRequestDto) {
		userService.create(userCreationRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
