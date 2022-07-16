package com.behl.strongbox.service.implementation;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.service.FileDetailService;
import com.behl.strongbox.service.StorageService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageFactory {

	private final AwsStorageService awsStorageService;
	private final AzureStorageService azureStorageService;
	private final GcpStorageService gcpStorageService;
	private final S3EmulatorService emulatorService;
	private final FileDetailService fileDetailService;

	/**
	 * 
	 * Returns StorageService implmentation corresponding to given platform
	 * 
	 * @param platform : Valid Enum value for com.behl.strongbox.constant.Platform
	 */
	public StorageService get(@NonNull final Platform platform) {
		if (Platform.AWS.equals(platform))
			return awsStorageService;
		else if (Platform.AZURE.equals(platform))
			return azureStorageService;
		else if (Platform.GCP.equals(platform))
			return gcpStorageService;
		else if (Platform.EMULATION.equals(platform))
			return emulatorService;
		else
			throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
	}

	/**
	 * Returns StorageService implmentation corresponding to given referenceId
	 * 
	 * @param referenceId: Valid UUID referenceId corresponding to saved File
	 */
	public StorageService get(@NonNull final UUID referenceId) {
		final var platform = fileDetailService.getById(referenceId).getPlatform();
		return get(platform);
	}

}
