package com.behl.strongbox.service.implementation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.behl.strongbox.configuration.properties.S3NinjaConfigurationProperties;
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
public class S3EmulatorStorageService implements StorageService {

	@Autowired(required = false)
	@Qualifier("emulatedAmazonS3")
	private AmazonS3 s3NinjaEmulator;

	private final S3NinjaConfigurationProperties s3NinjaConfigurationProperties;
	private final FileDetailService fileDetailService;

	/**
	 * @param file: represents an object to be saved in configured S3 Ninja endpoint
	 * @return HttpStatus 200 OK if file was saved.
	 */
	@Override
	public FileStorageSuccessDto save(final MultipartFile file, final Map<String, Object> customMetadata) {
		final var metadata = StorageUtility.constructMetadata(file);
		final var s3Properties = s3NinjaConfigurationProperties.getS3();
		try {
			final var putObjectRequest = new PutObjectRequest(s3Properties.getBucketName(), file.getOriginalFilename(),
					file.getInputStream(), metadata);
			s3NinjaEmulator.putObject(putObjectRequest);
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
	@Override
	public FileRetrievalDto retrieve(final UUID referenceId) {
		final var fileDetail = fileDetailService.getById(referenceId);
		final String bucketName = s3NinjaConfigurationProperties.getS3().getBucketName();
		final var getObjectRequest = new GetObjectRequest(bucketName, fileDetail.getContentDisposition());
		S3Object s3Object;
		try {
			s3Object = s3NinjaEmulator.getObject(getObjectRequest);
		} catch (final SdkClientException exception) {
			log.error("UNABLE TO RETIEVE FILE WITH KEY '{}' FROM S3 BUCKET '{}' : {}",
					fileDetail.getContentDisposition(), bucketName, LocalDateTime.now(), exception);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "UNABLE TO RETRIEVE FILE", exception);
		}

		return FileRetrievalDto.builder().fileContent(new InputStreamResource(s3Object.getObjectContent()))
				.fileName(s3Object.getKey()).build();
	}

	@Override
	public PresignedUrlResponseDto generatePresignedUrl(@NonNull UUID referenceId) {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,
				"FUNCTIONALITY NOT AVAILABLE CURRENTLY FOR S3 EMULATOR", new NotImplementedException());
	}

	@Override
	public void delete(@NonNull UUID referenceId) {
		final var fileDetail = fileDetailService.getById(referenceId);
		final var keyName = fileDetail.getContentDisposition();
		final var bucketName = s3NinjaConfigurationProperties.getS3().getBucketName();

		log.info("GENERATING DELETION REQUEST FOR '{}' STORED IN S3 NINJA EMULATION BUCKET {} : {}", keyName,
				bucketName, LocalDateTime.now());
		try {
			final var deleteObjectRequest = new DeleteObjectRequest(bucketName, keyName);
			s3NinjaEmulator.deleteObject(deleteObjectRequest);
		} catch (final SdkClientException exception) {
			log.error("UNABLE TO DELETE '{}' FROM S3 NINJA EMULATION BUCKET {} : {}", keyName, bucketName,
					LocalDateTime.now(), exception);
			throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "UNABLE TO DELETE OBJECT", exception);
		}
		fileDetailService.delete(referenceId);
		log.info("'{}' FROM S3 NINJA EMULATION BUCKET {} DELETED SUCCESSFULLY : {}", keyName, bucketName,
				LocalDateTime.now());
	}

}
