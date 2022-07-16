package com.behl.strongbox.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "com.behl.strongbox.digital-ocean.spaces")
public class DigitalOceanSpacesConfigurationProperties {

	/**
	 * Boolean value on if Digital Ocean Spaces is to be configured as a storage
	 * integration service. Disabled by default if no value is provided
	 */
	private Boolean enabled;
	/**
	 * Access Key in generated spaces access key pair
	 */
	private String accessKey;
	/**
	 * Secret Key in generated spaces access key pair
	 */
	private String secretKey;
	/**
	 * Name of created bucket in Digital Ocean Spaces. The provided bucket should be
	 * accessible with the configured spaces access key pair.
	 */
	private String bucketName;
	/**
	 * Endpoint corresponding to configured Digital Ocean Spaces
	 */
	private String endpoint;
	/**
	 * Region in which the Digital Ocean Spaces is created.
	 */
	private String region;

}
