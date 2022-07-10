package com.behl.strongbox.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "com.behl.strongbox.gcp")
public class GcpStorageConfigurationProperties {

	/**
	 * Boolean value on if GCP storage platform is to be configured as a storage
	 * integration service. Disabled by default if no value is provided
	 */
	private Boolean enabled;

	/**
	 * GCP Project-ID under which the storage bucket is configured
	 */
	private String projectId;

	/**
	 * Bucket name in Google Cloud Platform that will be used to store and retrieve
	 * files. Must be accessible by the authentication key being provided
	 */
	private String bucketName;

	/**
	 * Path to JSON Authentication key to establish connection and authorize against
	 * Google Cloud Platform.
	 */
	private String authenticationKeyPath;

}