package com.behl.strongbox.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "com.behl.strongbox.wasabi")
public class WasabiConfigurationProperties {

	/**
	 * Boolean value on if Wasabi [https://wasabi.com] is to be configured as a
	 * storage integration service. Disabled by default if no value is provided
	 */
	private Boolean enabled;
	private String accessKey;
	private String secretKey;
	/**
	 * Name of created bucket in Wasabi. The provided bucket should be accessible
	 * with the configured access key pair.
	 */
	private String bucketName;
	/**
	 * Region in which the the configured Wasabi bucket is created.
	 */
	private String region;
}
