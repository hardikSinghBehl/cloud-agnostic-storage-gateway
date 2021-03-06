package com.behl.strongbox.controller;

import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.behl.strongbox.annotation.CheckIfAuthorizedUser;
import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.FileStorageSuccessDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;
import com.behl.strongbox.service.FileDetailService;
import com.behl.strongbox.service.implementation.StorageFactory;
import com.behl.strongbox.utility.JsonUtil;
import com.behl.strongbox.utility.PlatformUtility;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/storage")
@RequiredArgsConstructor
public class StorageController {

	private final StorageFactory storageFactory;
	private final FileDetailService fileDetailService;
	private final PlatformUtility platformUtility;

	@CheckIfAuthorizedUser
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Uploads file to specified storage service")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "File saved Successfully to specified storage service"),
			@ApiResponse(responseCode = "417", description = "Unable to store file to specified storage service"),
			@ApiResponse(responseCode = "412", description = "Selected Platform is not enabled or not configured") })
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<FileStorageSuccessDto> save(
			@RequestPart(name = "file", required = true) final MultipartFile file,
			@RequestHeader(name = "X-CLOUD-PLATFORM", required = true) final Platform platform,
			@RequestPart(name = "customMetadata", required = false) final String customMetadata,
			@Parameter(hidden = true) @RequestHeader(name = "Authorization", required = true) final String accessToken) {
		platformUtility.validateIfEnabled(platform);
		final var storageService = storageFactory.get(platform);
		final var fileStorageResponse = storageService.save(file, JsonUtil.toMap(customMetadata));
		return ResponseEntity.status(HttpStatus.CREATED).body(fileStorageResponse);
	}

	@CheckIfAuthorizedUser
	@GetMapping(value = "/{referenceId}", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(description = "Retrieves file from storage service", summary = "Retrieves file corresponding to provided keyName from storage service")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Saved file retrived successfully"),
			@ApiResponse(responseCode = "404", description = "Unable to retieve file from storage service"),
			@ApiResponse(responseCode = "412", description = "Selected Platform is not enabled or not configured") })
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<InputStreamResource> retrieve(
			@PathVariable(name = "referenceId", required = true) final UUID referenceId,
			@Parameter(hidden = true) @RequestHeader(name = "Authorization", required = true) final String accessToken) {
		final var storageService = storageFactory.get(referenceId);
		final FileRetrievalDto fileRetrievalDto = storageService.retrieve(referenceId);
		return ResponseEntity.status(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileRetrievalDto.getFileName())
				.body(fileRetrievalDto.getFileContent());
	}

	@CheckIfAuthorizedUser
	@GetMapping(value = "/{referenceId}/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Retrieves Custom Metadata against referenceId", description = "Retrieves saved custom metadata for file against provided referenceId")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Saved Metadata retrived successfully for given referenceId"),
			@ApiResponse(responseCode = "404", description = "Invalid ReferenceId provided") })
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<?> retrieveCustomMetaData(
			@PathVariable(required = true, name = "referenceId") final UUID referenceId,
			@Parameter(hidden = true) @RequestHeader(name = "Authorization", required = true) final String accessToken) {
		return ResponseEntity.ok(fileDetailService.retrieveMetaDataById(referenceId));
	}

	@GetMapping(value = "/preview/{referenceId}")
	@Operation(summary = "Generates a Presigned-URL to grant temporary access to object. (Only AWS and Digital Ocean supported currently)", description = "Generates a Presigned-URL to grant temporary access to object corresponding to provided key, The Presigned-URL is valid for upto 10 minutes after generation")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Presigned-URL generated successfully"),
			@ApiResponse(responseCode = "417", description = "Unable to generate Presigned-URL") })
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<PresignedUrlResponseDto> generatePresignedUrl(
			@PathVariable(required = true, name = "referenceId") final UUID referenceId,
			@Parameter(hidden = true) @RequestHeader(name = "Authorization", required = true) final String accessToken) {
		final var storageService = storageFactory.get(referenceId);
		return ResponseEntity.ok(storageService.generatePresignedUrl(referenceId));
	}

	@CheckIfAuthorizedUser
	@DeleteMapping(value = "/{referenceId}")
	@Operation(summary = "Deletes file corresponding to referenceId", description = "Endpoint to delete objects from saved storage integration corresponding to referenceId provided")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "File corresponding to provided referenceId deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Invalid ReferenceId provided") })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<HttpStatus> deleteObject(
			@PathVariable(name = "referenceId", required = true) final UUID referenceId,
			@Parameter(hidden = true) @RequestHeader(name = "Authorization", required = true) final String accessToken) {
		final var storageService = storageFactory.get(referenceId);
		storageService.delete(referenceId);
		return ResponseEntity.noContent().build();
	}

}