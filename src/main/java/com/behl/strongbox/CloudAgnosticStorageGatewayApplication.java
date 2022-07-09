package com.behl.strongbox;

import java.time.LocalDateTime;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.amazonaws.util.StringUtils;
import com.behl.strongbox.configuration.properties.AwsConfigurationProperties;
import com.behl.strongbox.configuration.properties.AzureConfigurationProperties;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class CloudAgnosticStorageGatewayApplication {

	public static void main(String[] args) {
		var applicationContext = SpringApplication.run(CloudAgnosticStorageGatewayApplication.class, args);

		var awsConfigurationProperties = applicationContext.getBean(AwsConfigurationProperties.class);
		var azureConfigurationProperties = applicationContext.getBean(AzureConfigurationProperties.class);

		if (allDisabled(awsConfigurationProperties, azureConfigurationProperties)) {
			log.error(
					"Atleast 1 Storage-service provider must be enabled : {} : Go to 'application.properties' file to update configuration",
					LocalDateTime.now());
			exit();
		}

		validateConfiguration(awsConfigurationProperties);
	}

	private static void validateConfiguration(final AwsConfigurationProperties awsConfigurationProperties) {
		if (BooleanUtils.isTrue(awsConfigurationProperties.getEnabled())) {
			if (StringUtils.isNullOrEmpty(awsConfigurationProperties.getS3().getBucketName())) {
				log.error("Bucket-name cannot be left empty under property 'com.behl.strongbox.aws.s3.bucket-name'");
				exit();
			}
			if (!StringUtils.isNullOrEmpty(awsConfigurationProperties.getAccessKey())
					&& !StringUtils.isNullOrEmpty(awsConfigurationProperties.getSecretAccessKey())) {
				if (StringUtils.isNullOrEmpty(awsConfigurationProperties.getS3().getRegion())) {
					log.error("A valid region must be provided corresponding to configured S3 Bucket");
					exit();
				}
			}
		}
	}

	private static boolean allDisabled(final AwsConfigurationProperties awsConfigurationProperties,
			final AzureConfigurationProperties azureConfigurationProperties) {
		return (awsConfigurationProperties.getEnabled() == null
				|| BooleanUtils.isFalse(awsConfigurationProperties.getEnabled()))
				&& (azureConfigurationProperties.getEnabled() == null
						|| BooleanUtils.isFalse(azureConfigurationProperties.getEnabled()));
	}

	private static void exit() {
		System.exit(0);
	}

}
