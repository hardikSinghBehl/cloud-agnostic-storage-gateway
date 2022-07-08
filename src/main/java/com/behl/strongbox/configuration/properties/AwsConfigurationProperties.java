package com.behl.strongbox.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "com.behl.strongbox.aws")
public class AwsConfigurationProperties {

	/**
	 * Boolean value on if AWS S3 is to be configured as a storage integration
	 * service. Disabled by default if no value is provided
	 */
	private Boolean enabled;
	/**
	 * IAM user's access-key_id: to be used for authorization against the S3 Bucket
	 * being used. Can be ignored if using IAM Roles
	 */
	private String accessKey;
	/**
	 * IAM user's secret-access-key: to be used for authorization against the S3
	 * Bucket being used. Can be ignored if using IAM Roles
	 */
	private String secretAccessKey;

	private S3 s3 = new S3();

	@Data
	public class S3 {
		/**
		 * Name of default S3 bucket to be used to perform storage operations against.
		 * The provided bucket should be accessible with the configured IAM security
		 * credentials or permission to be granted via IAM Roles
		 */
		private String bucketName;
		/**
		 * Region in which the S3 bucket is created. If IAM Role is being used in EC2
		 * server(s) then the current region will be fetched automatically using the EC2
		 * Meta Data
		 */
		private String region;
	}

}
