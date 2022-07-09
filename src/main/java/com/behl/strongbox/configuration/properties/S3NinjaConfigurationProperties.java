package com.behl.strongbox.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "com.behl.strongbox.aws.emulated")
public class S3NinjaConfigurationProperties {

	/**
	 * Boolean value on if AWS S3ninja Emulator is to be configured as a storage
	 * integration service. Disabled by default if no value is provided
	 */
	private Boolean enabled;
	/**
	 * Dummy Access-key as displayed on the UI [<s3-ninja-hosted-ip>/ui]
	 */
	private String accessKey;
	/**
	 * Dummy Secret-Access-key as displayed on the UI [<s3-ninja-hosted-ip>/ui]
	 */
	private String secretAccessKey;

	private S3 s3 = new S3();

	@Data
	public class S3 {
		/**
		 * Name of default S3 bucket to be used to perform storage operations against.
		 * The provided bucket should be accessible with the configured security
		 * credentials.
		 */
		private String bucketName;
		/**
		 * Endpoint where emulated s3Ninja [https://s3ninja.net] container is being
		 * hosted
		 */
		private String endpoint;
	}

}
