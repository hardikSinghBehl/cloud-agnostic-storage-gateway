package com.behl.strongbox.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;
import com.behl.strongbox.service.implementation.AwsStorageService;
import com.behl.strongbox.service.implementation.AzureStorageService;
import com.behl.strongbox.utility.PlatformUtility;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/storage")
@RequiredArgsConstructor
public class StorageController {

	private final AwsStorageService awsStorageService;
	private final AzureStorageService azureStorageService;
	private final PlatformUtility platformUtility;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Uploads file to specified storage service")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "File saved Successfully to specified storage service"),
			@ApiResponse(responseCode = "417", description = "Unable to store file to specified storage service"),
			@ApiResponse(responseCode = "412", description = "Selected Platform is not enabled or not configured") })
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<HttpStatus> save(@RequestPart(name = "file", required = true) final MultipartFile file,
			@RequestHeader(name = "X-CLOUD-PLATFORM", required = true) final Platform platform) {
		platformUtility.validateIfEnabled(platform);
		final var response = Platform.AWS.equals(platform) ? awsStorageService.save(file)
				: azureStorageService.save(file);
		return ResponseEntity.status(response).build();
	}

	@GetMapping(value = "/{keyName}")
	@Operation(description = "Retrieves file from storage service", summary = "Retrieves file corresponding to provided keyName from storage service")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Saved file retrived successfully"),
			@ApiResponse(responseCode = "404", description = "Unable to retieve file from storage service"),
			@ApiResponse(responseCode = "412", description = "Selected Platform is not enabled or not configured") })
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<InputStreamResource> retrieve(
			@PathVariable(name = "keyName", required = true) final String keyName,
			@RequestHeader(name = "X-CLOUD-PLATFORM", required = true) final Platform platform) {
		platformUtility.validateIfEnabled(platform);
		final FileRetrievalDto fileRetrievalDto = Platform.AWS.equals(platform) ? awsStorageService.retrieve(keyName)
				: azureStorageService.retrieve(keyName);
		return ResponseEntity.status(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileRetrievalDto.getFileName())
				.body(fileRetrievalDto.getFileContent());
	}

	@GetMapping(value = "/preview/{keyName}")
	@Operation(summary = "Generates a Presigned-URL to grant temporary access to object", description = "Generates a Presigned-URL to grant temporary access to object corresponding to provided key, The Presigned-URL is valid for upto 10 minutes after generation")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Presigned-URL generated successfully"),
			@ApiResponse(responseCode = "417", description = "Unable to generate Presigned-URL") })
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<PresignedUrlResponseDto> generatePresignedUrl(
			@PathVariable(required = true, name = "keyName") final String keyName) {
		return ResponseEntity.ok(awsStorageService.generatePresignedUrl(keyName));
	}

}