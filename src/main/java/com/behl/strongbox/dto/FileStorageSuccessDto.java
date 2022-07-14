package com.behl.strongbox.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class FileStorageSuccessDto {

	private final UUID referenceId;

	@Builder.Default
	private final LocalDateTime timestamp = LocalDateTime.now();

}
