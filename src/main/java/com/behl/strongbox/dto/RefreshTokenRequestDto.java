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
public class RefreshTokenRequestDto implements Serializable {

	private static final long serialVersionUID = 7278113015247374755L;

	@Schema(description = "refresh-token received during successfull authentication")
	@NotBlank(message = "Refresh token must not be empty")
	private final String refreshToken;

}
