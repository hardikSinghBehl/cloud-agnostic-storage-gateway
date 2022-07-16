package com.behl.strongbox.service.implementation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.behl.strongbox.configuration.properties.GcpStorageConfigurationProperties;
import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.FileStorageSuccessDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;
import com.behl.strongbox.service.FileDetailService;
import com.behl.strongbox.service.StorageService;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@EnableConfigurationProperties(value = GcpStorageConfigurationProperties.class)
@RequiredArgsConstructor
public class GcpStorageService implements StorageService {

	@Autowired(required = false)
	private Storage gcpStorage;

	private final GcpStorageConfigurationProperties gcpStorageConfigurationProperties;
	private final FileDetailService fileDetailService;

	@Override
	public FileStorageSuccessDto save(@NonNull final MultipartFile file, final Map<String, Object> customMetadata) {
		final String bucketName = gcpStorageConfigurationProperties.getBucketName();
		log.info("Uploading '{}' to configured GCP Bucket '{}' : {}", file.getOriginalFilename(), bucketName,
				LocalDateTime.now());
		try {
			gcpStorage.create(BlobInfo.newBuilder(bucketName, file.getOriginalFilename()).build(),
					file.getInputStream().readAllBytes());
		} catch (final IOException exception) {
			log.error("Exception occurred during '{}' Upload Attempt to configured GCP Bucket '{}' : {}",
					file.getOriginalFilename(), bucketName, LocalDateTime.now(), exception);
			throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED,
					"UNABLE TO STORE FILE TO CONFIGURED GCP BUCKET", exception);
		}
		final UUID savedFileDetailId = fileDetailService.save(file, customMetadata, Platform.GCP,
				gcpStorageConfigurationProperties.getBucketName());
		return FileStorageSuccessDto.builder().referenceId(savedFileDetailId).build();
	}

	@Override
	public FileRetrievalDto retrieve(@NonNull UUID referenceId) {
		final var fileDetail = fileDetailService.getById(referenceId);
		final String bucketName = gcpStorageConfigurationProperties.getBucketName();
		log.info("Attempting to Retrieve '{}' from configured GCP Bucket '{}' : {}", fileDetail.getContentDisposition(),
				bucketName, LocalDateTime.now());
		byte[] fileContent;
		try {
			Blob blob = gcpStorage.get(BlobId.of(bucketName, fileDetail.getContentDisposition()));
			fileContent = blob.getContent(Blob.BlobSourceOption.generationMatch());
		} catch (final StorageException exception) {
			log.error("Exception Occurred While Attempting To Retieve '{}' From Configured GCP Bucket {}: {}",
					fileDetail.getContentDisposition(), bucketName, LocalDateTime.now(), exception);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "UNABLE TO RETRIVE FILE TO CONFIGURED GCP BUCKET",
					exception);
		}
		return FileRetrievalDto.builder().fileName(fileDetail.getContentDisposition())
				.fileContent(new InputStreamResource(new ByteArrayInputStream(fileContent))).build();
	}

	@Override
	public PresignedUrlResponseDto generatePresignedUrl(@NonNull UUID referenceId) {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "FUNCTIONALITY NOT AVAILABLE CURRENTLY FOR GCP",
				new NotImplementedException());
	}

}
