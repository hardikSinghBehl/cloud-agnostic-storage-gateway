package com.behl.strongbox.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.service.StorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/storage")
@RequiredArgsConstructor
public class StorageController {

	private final StorageService storageService;

	@PostMapping(value = "/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(description = "Uploads file to specified storage service")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "File saved Successfully to specified storage service"),
			@ApiResponse(responseCode = "417", description = "Unable to store file to specified storage service") })
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<HttpStatus> save(@RequestPart(name = "file", required = true) final MultipartFile file) {
		return ResponseEntity.status(storageService.save(file)).build();
	}

	@GetMapping(value = "/v1/{keyName}")
	@Operation(description = "Retrieves file from storage service", summary = "Retrieves file corresponding to provided keyName from storage service")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Saved file retrived successfully"),
			@ApiResponse(responseCode = "404", description = "Unable to retieve file from storage service") })
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<InputStreamResource> retrieve(
			@PathVariable(name = "keyName", required = true) final String keyName) {
		final FileRetrievalDto fileRetrievalDto = storageService.retrieve(keyName);
		return ResponseEntity.status(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileRetrievalDto.getFileName())
				.body(fileRetrievalDto.getFileContent());
	}

}