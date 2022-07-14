package com.behl.strongbox.service.implementation;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.FileStorageSuccessDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;
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

	@Override
	public FileStorageSuccessDto save(@NonNull Platform platform, @NonNull MultipartFile file) {
		if (Platform.AWS.equals(platform))
			return awsStorageService.save(file);
		else if (Platform.AZURE.equals(platform))
			return azureStorageService.save(file);
		else if (Platform.GCP.equals(platform))
			return gcpStorageService.save(file);
		else if (Platform.EMULATION.equals(platform))
			return emulatorService.save(file);
		else
			throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public FileRetrievalDto retrieve(@NonNull Platform platform, @NonNull UUID referenceId) {
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
	public PresignedUrlResponseDto generatePresignedUrl(@NonNull Platform platform, @NonNull String keyName) {
		if (Platform.AWS.equals(platform))
			return awsStorageService.generatePresignedUrl(keyName);
		log.error("Presigned-URL generation called for unsupported cloud storage provider {} : {}", platform.name(),
				LocalDateTime.now());
		throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
				"PRESIGNED-URL FUNCTIONALITY IS ONLY APPLICABLE FOR AWS S3 FOR NOW : {}");
	}

}
