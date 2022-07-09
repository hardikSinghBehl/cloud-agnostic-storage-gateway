package com.behl.strongbox.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.behl.strongbox.constant.AccountType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class UserCreationRequestDto implements Serializable {

	private static final long serialVersionUID = -6117448062182178039L;

	@NotBlank(message = "userName must not be empty")
	@Schema(required = true, description = "userName to be associated with new account", example = "user-microservice", maxLength = 15, minLength = 3)
	private final String userName;

	@NotNull(message = "account-type must not be empty")
	@Schema(required = true, description = "type of user-account being created")
	private final AccountType accountType;

	@NotBlank(message = "password must not be empty")
	@Schema(required = true, description = "secure password to enable user-account authentication", example = "somethingSecure")
	private final String password;

}
