package com.behl.strongbox.service.implementation;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.FileStorageSuccessDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;
import com.behl.strongbox.service.FileDetailService;
import com.behl.strongbox.service.StorageService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

	private final AwsStorageService awsStorageService;
	private final AzureStorageService azureStorageService;
	private final GcpStorageService gcpStorageService;
	private final S3EmulatorService emulatorService;
	private final FileDetailService fileDetailService;

	@Override
	public FileStorageSuccessDto save(@NonNull Platform platform, @NonNull MultipartFile file,
			final Map<String, Object> customMetadata) {
		if (Platform.AWS.equals(platform))
			return awsStorageService.save(file, customMetadata);
		else if (Platform.AZURE.equals(platform))
			return azureStorageService.save(file, customMetadata);
		else if (Platform.GCP.equals(platform))
			return gcpStorageService.save(file, customMetadata);
		else if (Platform.EMULATION.equals(platform))
			return emulatorService.save(file, customMetadata);
		else
			throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public FileRetrievalDto retrieve(@NonNull UUID referenceId) {
		final var fileDetail = fileDetailService.getById(referenceId);
		final var platform = fileDetail.getPlatform();
		if (Platform.AWS.equals(platform))
			return awsStorageService.retrieve(referenceId);
		else if (Platform.AZURE.equals(platform))
			return azureStorageService.retrieve(referenceId);
		else if (Platform.GCP.equals(platform))
			return gcpStorageService.retrieve(referenceId);
		else if (Platform.EMULATION.equals(platform))
			return emulatorService.retrieve(referenceId);
		else
			throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public PresignedUrlResponseDto generatePresignedUrl(@NonNull UUID referenceId) {
		final var fileDetail = fileDetailService.getById(referenceId);
		final var platform = fileDetail.getPlatform();
		if (Platform.AWS.equals(platform))
			return awsStorageService.generatePresignedUrl(fileDetail.getContentDisposition());
		log.error("Presigned-URL generation called for unsupported cloud storage provider {} : {}", platform.name(),
				LocalDateTime.now());
		throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
				"PRESIGNED-URL FUNCTIONALITY IS ONLY APPLICABLE FOR AWS S3 FOR NOW : {}");
	}

}
