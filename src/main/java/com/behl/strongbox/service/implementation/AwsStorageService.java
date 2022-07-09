package com.behl.strongbox.service.implementation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.behl.strongbox.configuration.properties.AwsConfigurationProperties;
import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;
import com.behl.strongbox.utility.S3Utility;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AwsStorageService {

	@Autowired(required = false)
	private AmazonS3 amazonS3;

	@Autowired
	private AwsConfigurationProperties awsConfigurationProperties;

	/**
	 * @param file: represents an object to be saved in configured S3 Bucket
	 * @return HttpStatus 200 OK if file was saved.
	 */
	public HttpStatus save(final MultipartFile file) {
		final var metadata = S3Utility.constructMetadata(file);
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
				.fileName(s3Object.getObjectMetadata().getContentDisposition()).build();
	}

	/**
	 * Generates a Presigned URL to enable access to preview object corresponding to
	 * provided key for 10 minutes
	 * 
	 * @param keyName: object key corresponding to which Pre-signed URL is to be
	 *                 generated
	 * @return PresignedUrlResponseDto.class containing the presigned-URL for the
	 *         specified object and the valid until timestamp
	 */
	public PresignedUrlResponseDto generatePresignedUrl(final String keyName) {
		final var s3Properties = awsConfigurationProperties.getS3();
		final var validUntilTimestamp = LocalDateTime.now().plusMinutes(10);

		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
				s3Properties.getBucketName(), keyName, HttpMethod.GET);
		generatePresignedUrlRequest.setExpiration(Date.from(validUntilTimestamp.toInstant(ZoneOffset.UTC)));

		try {
			final URL presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
			return PresignedUrlResponseDto.builder().url(presignedUrl.toURI().toString())
					.validUntil(validUntilTimestamp).build();
		} catch (final SdkClientException | URISyntaxException exception) {
			log.error("EXCEPTION OCCURRED WHILE GENERATING PRE-SIGNED URL FOR '{}' IN S3 BUCKET {} : {}", keyName,
					s3Properties.getBucketName(), LocalDateTime.now(), exception);
			throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "UNABLE TO GENERATE PRE-SIGNED URL",
					exception);
		}
	}

}
