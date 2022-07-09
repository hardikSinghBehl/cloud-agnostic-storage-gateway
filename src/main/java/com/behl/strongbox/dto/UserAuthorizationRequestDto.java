package com.behl.strongbox.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class UserAuthorizationRequestDto implements Serializable {

	private static final long serialVersionUID = -3243809542585061706L;

	@NotBlank(message = "userName must not be empty")
	@Schema(required = true, name = "userName", example = "user-microservice", description = "userName associated with user account already created in the system")
	private final String userName;

	@NotBlank(message = "password must not be empty")
	@Schema(required = true, example = "somethingSecure", description = "password corresponding to provided userName")
	private final String password;

}
