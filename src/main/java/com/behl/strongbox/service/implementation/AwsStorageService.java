package com.behl.strongbox.service.implementation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

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
import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.FileStorageSuccessDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;
import com.behl.strongbox.service.FileDetailService;
import com.behl.strongbox.service.StorageService;
import com.behl.strongbox.utility.StorageUtility;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsStorageService implements StorageService {

	@Autowired(required = false)
	private AmazonS3 amazonS3;

	private final AwsConfigurationProperties awsConfigurationProperties;
	private final FileDetailService fileDetailService;

	/**
	 * @param file: represents an object to be saved in configured S3 Bucket
	 * @return HttpStatus 200 OK if file was saved.
	 */
	@Override
	public FileStorageSuccessDto save(final MultipartFile file, final Map<String, Object> customMetadata) {
		final var metadata = StorageUtility.constructMetadata(file);
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
		final UUID savedFileDetailId = fileDetailService.save(file, customMetadata, Platform.AWS,
				awsConfigurationProperties.getS3().getBucketName());
		return FileStorageSuccessDto.builder().referenceId(savedFileDetailId).build();
	}

	/**
	 * Retrieves the file from configured S3 Bucket corresponding to provided
	 * referenceId
	 * 
	 * @return com.behl.strongbox.dto.FileRetrievalDto.class
	 */
	@Override
	public FileRetrievalDto retrieve(final UUID referenceId) {
		final var fileDetail = fileDetailService.getById(referenceId);
		final String bucketName = awsConfigurationProperties.getS3().getBucketName();
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
				.fileName(s3Object.getObjectMetadata().getContentDisposition()).build();
	}

	/**
	 * Generates a Presigned URL to enable access to preview object corresponding to
	 * provided key for 10 minutes
	 * 
	 * @param referenceId: saved referenceId corresponding to which Pre-signed URL
	 *                     is to be generated
	 * @return PresignedUrlResponseDto.class containing the presigned-URL for the
	 *         specified object and the valid until timestamp
	 */
	@Override
	public PresignedUrlResponseDto generatePresignedUrl(@NonNull final UUID referenceId) {
		final var fileDetail = fileDetailService.getById(referenceId);
		final var keyName = fileDetail.getContentDisposition();
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
