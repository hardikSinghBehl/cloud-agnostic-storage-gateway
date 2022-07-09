package com.behl.strongbox.dto;

import org.springframework.core.io.InputStreamResource;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileRetrievalDto {

	private final InputStreamResource fileContent;
	private final String fileName;

}
