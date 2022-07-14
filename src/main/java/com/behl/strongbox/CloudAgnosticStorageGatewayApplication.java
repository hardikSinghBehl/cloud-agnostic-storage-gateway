package com.behl.strongbox;

import java.time.LocalDateTime;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.amazonaws.util.StringUtils;
import com.behl.strongbox.configuration.properties.AwsConfigurationProperties;
import com.behl.strongbox.configuration.properties.AzureConfigurationProperties;
import com.behl.strongbox.configuration.properties.GcpStorageConfigurationProperties;
import com.behl.strongbox.configuration.properties.S3NinjaConfigurationProperties;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@EnableMongoRepositories(basePackages = "com.behl.strongbox.repository")
public class CloudAgnosticStorageGatewayApplication {

	public static void main(String[] args) {
		var applicationContext = SpringApplication.run(CloudAgnosticStorageGatewayApplication.class, args);

		var awsConfigurationProperties = applicationContext.getBean(AwsConfigurationProperties.class);
		var azureConfigurationProperties = applicationContext.getBean(AzureConfigurationProperties.class);
		var gcpConfigurationProperties = applicationContext.getBean(GcpStorageConfigurationProperties.class);
		var s3NinjaEmulatorConfigurationProperties = applicationContext.getBean(S3NinjaConfigurationProperties.class);

		if (allDisabled(awsConfigurationProperties, azureConfigurationProperties, gcpConfigurationProperties,
				s3NinjaEmulatorConfigurationProperties)) {
			log.error(
					"Atleast 1 Storage-service provider must be enabled : {} : Go to 'application.properties' file to update configuration",
					LocalDateTime.now());
			exit();
		}

		validateConfiguration(awsConfigurationProperties);
		validateConfiguration(azureConfigurationProperties);
		validateConfiguration(gcpConfigurationProperties);
		validateConfiguration(s3NinjaEmulatorConfigurationProperties);
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

	private static void validateConfiguration(final AzureConfigurationProperties azureConfigurationProperties) {
		if (BooleanUtils.isTrue(azureConfigurationProperties.getEnabled())) {
			if (StringUtils.isNullOrEmpty(azureConfigurationProperties.getContainer())) {
				log.error("container name cannot be left empty under property 'com.behl.strongbox.azure.container'");
				exit();
			}
			if (StringUtils.isNullOrEmpty(azureConfigurationProperties.getConnectionString())
					&& (StringUtils.isNullOrEmpty(azureConfigurationProperties.getSasToken())
							&& StringUtils.isNullOrEmpty(azureConfigurationProperties.getSasUrl()))
					|| (StringUtils.isNullOrEmpty(azureConfigurationProperties.getConnectionString())
							&& (StringUtils.isNullOrEmpty(azureConfigurationProperties.getSasUrl())
									|| StringUtils.isNullOrEmpty(azureConfigurationProperties.getSasToken())))) {
				log.error(
						"Either a connection string or a combination of SAS Token and SAS URL have to be configured to enable connection to configured container");
				exit();
			}
		}
	}

	private static void validateConfiguration(final GcpStorageConfigurationProperties gcpConfigurationProperties) {
		if (BooleanUtils.isTrue(gcpConfigurationProperties.getEnabled())) {
			if (StringUtils.isNullOrEmpty(gcpConfigurationProperties.getBucketName())
					|| StringUtils.isNullOrEmpty(gcpConfigurationProperties.getAuthenticationKeyPath())
					|| StringUtils.isNullOrEmpty(gcpConfigurationProperties.getProjectId())) {
				log.error(
						"All configuration values must be present for GCP Storage under 'com.behl.strongbox.gcp.*' in application.properties file. Refer {}",
						GcpStorageConfigurationProperties.class.getName());
				exit();
			}
		}
	}

	private static void validateConfiguration(final S3NinjaConfigurationProperties configuration) {
		if (BooleanUtils.isTrue(configuration.getEnabled())) {
			if (StringUtils.isNullOrEmpty(configuration.getAccessKey())
					|| StringUtils.isNullOrEmpty(configuration.getSecretAccessKey())
					|| StringUtils.isNullOrEmpty(configuration.getS3().getBucketName())
					|| StringUtils.isNullOrEmpty(configuration.getS3().getEndpoint())) {
				log.error(
						"All configuration values must be present for S3Ninja emulator under 'com.behl.strongbox.aws.emulated.*' in application.properties file. Refer {}",
						S3NinjaConfigurationProperties.class.getName());
				exit();
			}
		}
	}

	private static boolean allDisabled(final AwsConfigurationProperties awsConfigurationProperties,
			final AzureConfigurationProperties azureConfigurationProperties,
			final GcpStorageConfigurationProperties gcpStorageConfigurationProperties,
			final S3NinjaConfigurationProperties s3NinjaConfigurationProperties) {
		return (awsConfigurationProperties.getEnabled() == null
				|| BooleanUtils.isFalse(awsConfigurationProperties.getEnabled()))
				&& (azureConfigurationProperties.getEnabled() == null
						|| BooleanUtils.isFalse(azureConfigurationProperties.getEnabled()))
				&& (gcpStorageConfigurationProperties.getEnabled() == null
						|| BooleanUtils.isFalse(gcpStorageConfigurationProperties.getEnabled()))
				&& (s3NinjaConfigurationProperties.getEnabled() == null
						|| BooleanUtils.isFalse(s3NinjaConfigurationProperties.getEnabled()));
	}

	private static void exit() {
		System.exit(0);
	}

}
