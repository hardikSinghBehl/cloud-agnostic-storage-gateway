package com.behl.strongbox.exception.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class ExceptionResponseDto {

	private final Integer status;
	private final String message;
	private final String description;
	private final LocalDateTime timestamp;

}
