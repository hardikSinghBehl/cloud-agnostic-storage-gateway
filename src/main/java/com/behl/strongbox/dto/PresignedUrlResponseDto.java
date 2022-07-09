package com.behl.strongbox.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class PresignedUrlResponseDto {

	private final String url;
	private final LocalDateTime validUntil;

}
