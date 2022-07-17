package com.behl.strongbox.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.util.StringUtils;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.behl.strongbox.configuration.properties.AzureConfigurationProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties(value = AzureConfigurationProperties.class)
public class AzureStorageConfiguration {

	@Bean
	@Primary
	public BlobContainerClient blobContainerClient(final AzureConfigurationProperties azureConfigurationProperties) {
		if (Boolean.TRUE.equals(azureConfigurationProperties.getEnabled())) {
			BlobServiceClient blobServiceClient;
			if (StringUtils.isNullOrEmpty(azureConfigurationProperties.getConnectionString())) {
				blobServiceClient = new BlobServiceClientBuilder().sasToken(azureConfigurationProperties.getSasToken())
						.endpoint(azureConfigurationProperties.getSasUrl()).buildClient();
			} else {
				blobServiceClient = new BlobServiceClientBuilder()
						.connectionString(azureConfigurationProperties.getConnectionString()).buildClient();
			}
			log.info("Azure Blob Storage Integration Configured Successfully");
			return blobServiceClient.getBlobContainerClient(azureConfigurationProperties.getContainer());
		}
		log.warn("Azure Blob Storage Integration Not Configured");
		return null;
	}

}