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

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = AzureConfigurationProperties.class)
public class AzureStorageConfiguration {

	private final AzureConfigurationProperties azureConfigurationProperties;

	@Bean
	@Primary
	public BlobContainerClient blobContainerClient() {
		if (Boolean.TRUE.equals(azureConfigurationProperties.getEnabled())) {
			BlobServiceClient blobServiceClient = null;
			if (StringUtils.isNullOrEmpty(azureConfigurationProperties.getConnectionString())) {
				blobServiceClient = new BlobServiceClientBuilder().sasToken(azureConfigurationProperties.getSasToken())
						.endpoint(azureConfigurationProperties.getSasUrl()).buildClient();
			} else {
				blobServiceClient = new BlobServiceClientBuilder()
						.connectionString(azureConfigurationProperties.getConnectionString()).buildClient();
			}
			return blobServiceClient.getBlobContainerClient(azureConfigurationProperties.getContainer());
		}
		return null;
	}

}
