package com.behl.strongbox.service.implementation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.behl.strongbox.configuration.properties.DigitalOceanSpacesConfigurationProperties;
import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.FileStorageSuccessDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;
import com.behl.strongbox.service.FileDetailService;
import com.behl.strongbox.service.StorageService;
import com.behl.strongbox.utility.S3Utility;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(value = DigitalOceanSpacesConfigurationProperties.class)
public class DigitalOceanSpaceService implements StorageService {

	@Autowired(required = false)
	@Qualifier("digitalOceanSpace")
	private AmazonS3 digitalOceanSpace;

	private final DigitalOceanSpacesConfigurationProperties digitalOceanSpacesConfigurationProperties;
	private final FileDetailService fileDetailService;

	@Override
	public FileStorageSuccessDto save(@NonNull MultipartFile file, Map<String, Object> customMetadata) {
		final var metadata = S3Utility.constructMetadata(file);
		final var bucketName = digitalOceanSpacesConfigurationProperties.getBucketName();
		try {
			final var putObjectRequest = new PutObjectRequest(bucketName, file.getOriginalFilename(),
					file.getInputStream(), metadata);
			digitalOceanSpace.putObject(putObjectRequest);
		} catch (final SdkClientException | IOException exception) {
			log.error("UNABLE TO STORE {} IN DIGITAL OCEAN SPACES BUCKET {} : {}", file.getOriginalFilename(),
					bucketName, LocalDateTime.now(), exception);
			throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED,
					"UNABLE TO STORE FILE TO CONFIGURED DIGITAL OCEAN SPACES", exception);
		}
		final UUID savedFileDetailId = fileDetailService.save(file, customMetadata, Platform.DIGITAL_OCEAN_SPACES,
				bucketName);
		return FileStorageSuccessDto.builder().referenceId(savedFileDetailId).build();
	}

	@Override
	public FileRetrievalDto retrieve(@NonNull UUID referenceId) {
		final var fileDetail = fileDetailService.getById(referenceId);
		final var bucketName = digitalOceanSpacesConfigurationProperties.getBucketName();
		final var getObjectRequest = new GetObjectRequest(bucketName, fileDetail.getContentDisposition());
		S3Object retrievedFile;
		try {
			retrievedFile = digitalOceanSpace.getObject(getObjectRequest);
		} catch (final SdkClientException exception) {
			log.error("UNABLE TO RETIEVE FILE WITH KEY '{}' FROM DIGITAL OCEAN SPACES BUCKET '{}' : {}",
					fileDetail.getContentDisposition(), bucketName, LocalDateTime.now(), exception);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "UNABLE TO RETRIEVE FILE", exception);
		}

		return FileRetrievalDto.builder().fileContent(new InputStreamResource(retrievedFile.getObjectContent()))
				.fileName(retrievedFile.getKey()).build();
	}

	@Override
	public PresignedUrlResponseDto generatePresignedUrl(@NonNull UUID referenceId) {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,
				"FUNCTIONALITY NOT AVAILABLE CURRENTLY FOR DIGITAL OCEAN SPACES", new NotImplementedException());
	}

}
