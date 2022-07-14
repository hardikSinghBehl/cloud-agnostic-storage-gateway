package com.behl.strongbox.service.implementation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.behl.strongbox.configuration.properties.S3NinjaConfigurationProperties;
import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.FileStorageSuccessDto;
import com.behl.strongbox.service.FileDetailService;
import com.behl.strongbox.utility.S3Utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3EmulatorService {

	@Autowired(required = false)
	@Qualifier("emulatedAmazonS3")
	private AmazonS3 amazonS3;

	private final S3NinjaConfigurationProperties s3NinjaConfigurationProperties;
	private final FileDetailService fileDetailService;

	/**
	 * @param file: represents an object to be saved in configured S3 Ninja endpoint
	 * @return HttpStatus 200 OK if file was saved.
	 */
	public FileStorageSuccessDto save(final MultipartFile file, final Map<String, Object> customMetadata) {
		final var metadata = S3Utility.constructMetadata(file);
		final var s3Properties = s3NinjaConfigurationProperties.getS3();
		try {
			final var putObjectRequest = new PutObjectRequest(s3Properties.getBucketName(), file.getOriginalFilename(),
					file.getInputStream(), metadata);
			amazonS3.putObject(putObjectRequest);
		} catch (final SdkClientException | IOException exception) {
			log.error("UNABLE TO STORE {} IN S3 BUCKET {} : {} ", file.getOriginalFilename(),
					s3Properties.getBucketName(), LocalDateTime.now(), exception);
			throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED,
					"UNABLE TO STORE FILE TO CONFIGURED S3 BUCKET", exception);
		}
		final UUID savedFileDetailId = fileDetailService.save(file, customMetadata, Platform.EMULATION,
				s3NinjaConfigurationProperties.getS3().getBucketName());
		return FileStorageSuccessDto.builder().referenceId(savedFileDetailId).build();
	}

	/**
	 * Retrieves the file from configured S3 Ninja Endpoint corresponding to
	 * provided keyName
	 * 
	 * @return com.behl.strongbox.dto.FileRetrievalDto.class
	 */
	public FileRetrievalDto retrieve(final UUID referenceId) {
		final var fileDetail = fileDetailService.getById(referenceId);
		final String bucketName = s3NinjaConfigurationProperties.getS3().getBucketName();
		final var getObjectRequest = new GetObjectRequest(bucketName, fileDetail.getContentDisposition());
		S3Object s3Object;
		try {
			s3Object = amazonS3.getObject(getObjectRequest);
		} catch (final SdkClientException exception) {
			log.error("UNABLE TO RETIEVE FILE WITH KEY '{}' FROM S3 BUCKET '{}' : {}",
					fileDetail.getContentDisposition(), bucketName, LocalDateTime.now(), exception);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "UNABLE TO RETRIEVE FILE", exception);
		}

		return FileRetrievalDto.builder().fileContent(new InputStreamResource(s3Object.getObjectContent()))
				.fileName(s3Object.getKey()).build();
	}

}
