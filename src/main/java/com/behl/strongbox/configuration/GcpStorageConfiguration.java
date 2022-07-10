package com.behl.strongbox.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.util.StringUtils;
import com.behl.strongbox.configuration.properties.GcpStorageConfigurationProperties;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = GcpStorageConfigurationProperties.class)
public class GcpStorageConfiguration {

	private final GcpStorageConfigurationProperties gcpStorageConfigurationProperties;

	@Bean
	public Storage gcpStorage() {
		if (BooleanUtils.isTrue(gcpStorageConfigurationProperties.getEnabled())
				&& !StringUtils.isNullOrEmpty(gcpStorageConfigurationProperties.getAuthenticationKeyPath())
				&& !StringUtils.isNullOrEmpty(gcpStorageConfigurationProperties.getProjectId())) {
			try {
				Credentials credentials = GoogleCredentials
						.fromStream(new FileInputStream(gcpStorageConfigurationProperties.getAuthenticationKeyPath()));
				return StorageOptions.newBuilder().setCredentials(credentials)
						.setProjectId(gcpStorageConfigurationProperties.getProjectId()).build().getService();
			} catch (final IOException exception) {
				log.error("Unable to create GCP Storage bean due to invalid configuration : {}", LocalDateTime.now(),
						exception);
			}
		}
		log.info("GCP Storage Integration Not Configured");
		return null;
	}

}
