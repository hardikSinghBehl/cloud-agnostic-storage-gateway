package com.behl.strongbox.service.implementation;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.behl.strongbox.configuration.properties.AwsConfigurationProperties;
import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.service.StorageService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AwsStorageService implements StorageService {

	@Autowired(required = false)
	private AmazonS3 amazonS3;

	@Autowired
	private AwsConfigurationProperties awsConfigurationProperties;

	/**
	 * @param file: represents an object to be saved in configured S3 Bucket
	 * @return HttpStatus 200 OK if file was saved.
	 */
	@Override
	public HttpStatus save(final MultipartFile file) {
		final var metadata = constructMetadata(file);
		final var s3Properties = awsConfigurationProperties.getS3();
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
		return HttpStatus.CREATED;
	}

	/**
	 * Retrieves the file from configured S3 Bucket corresponding to provided
	 * keyName
	 * 
	 * @return com.behl.strongbox.dto.FileRetrievalDto.class
	 */
	@Override
	public FileRetrievalDto retrieve(final String keyName) {
		final String bucketName = awsConfigurationProperties.getS3().getBucketName();
		final var getObjectRequest = new GetObjectRequest(bucketName, keyName);
		S3Object s3Object;
		try {
			s3Object = amazonS3.getObject(getObjectRequest);
		} catch (final SdkClientException exception) {
			log.error("UNABLE TO RETIEVE FILE WITH KEY '{}' FROM S3 BUCKET '{}' : {}", keyName, bucketName,
					LocalDateTime.now(), exception);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "UNABLE TO RETRIEVE FILE", exception);
		}

		return FileRetrievalDto.builder().fileContent(new InputStreamResource(s3Object.getObjectContent()))
				.fileName(s3Object.getKey()).build();
	}

	private ObjectMetadata constructMetadata(final MultipartFile file) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.getSize());
		metadata.setContentType(file.getContentType());
		metadata.setContentDisposition(file.getOriginalFilename());
		return metadata;
	}

}
