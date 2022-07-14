package com.behl.strongbox.service.implementation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.azure.storage.blob.BlobContainerClient;
import com.behl.strongbox.configuration.properties.AzureConfigurationProperties;
import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.FileStorageSuccessDto;
import com.behl.strongbox.service.FileDetailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AzureStorageService {

	@Autowired(required = false)
	private BlobContainerClient blobContainerClient;

	private final AzureConfigurationProperties azureConfigurationProperties;
	private final FileDetailService fileDetailService;

	public FileStorageSuccessDto save(final MultipartFile file) {
		log.info("Uploading '{}' to configured Azure Container : {}", file.getOriginalFilename(), LocalDateTime.now());
		var blobClient = blobContainerClient.getBlobClient(file.getOriginalFilename());
		try {
			blobClient.upload(file.getInputStream(), file.getSize(), true);
		} catch (final Exception exception) {
			log.error("UNABLE TO STORE '{}' TO AZURE CONTAINER '{}' : {}", file.getOriginalFilename(),
					blobClient.getContainerName(), LocalDateTime.now(), exception);
			throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED,
					"UNABLE TO STORE FILE TO CONFIGURED AZURE CONTAINER", exception);
		}
		final UUID savedFileDetailId = fileDetailService.save(file, Platform.AZURE,
				azureConfigurationProperties.getContainer());
		return FileStorageSuccessDto.builder().referenceId(savedFileDetailId).build();
	}

	public FileRetrievalDto retrieve(final UUID referenceId) {
		final var fileDetail = fileDetailService.getById(referenceId);
		log.info("Retrieving '{}' from Azure Container '{}' : {}", fileDetail.getContentDisposition(),
				blobContainerClient.getBlobContainerName(), LocalDateTime.now());
		var blobClient = blobContainerClient.getBlobClient(fileDetail.getContentDisposition());
		var outputStream = new ByteArrayOutputStream();
		try {
			blobClient.downloadStream(outputStream);
		} catch (final Exception exception) {
		}
		return FileRetrievalDto.builder().fileName(fileDetail.getContentDisposition())
				.fileContent(new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray()))).build();
	}

}
