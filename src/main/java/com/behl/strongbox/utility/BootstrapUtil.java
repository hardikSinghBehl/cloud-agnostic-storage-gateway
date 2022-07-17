package com.behl.strongbox.utility;

import org.apache.commons.lang3.BooleanUtils;

import com.amazonaws.util.StringUtils;
import com.behl.strongbox.configuration.properties.AwsConfigurationProperties;
import com.behl.strongbox.configuration.properties.AzureConfigurationProperties;
import com.behl.strongbox.configuration.properties.DigitalOceanSpacesConfigurationProperties;
import com.behl.strongbox.configuration.properties.GcpStorageConfigurationProperties;
import com.behl.strongbox.configuration.properties.S3NinjaConfigurationProperties;
import com.behl.strongbox.configuration.properties.WasabiConfigurationProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BootstrapUtil {

	private BootstrapUtil() {
	}

	public static Validator getValidator() {
		return new Validator();
	}

	public static class Validator {

		private Validator() {
		}

		public void validate(final AwsConfigurationProperties configuration) {
			if (BooleanUtils.isTrue(configuration.getEnabled())) {
				if (StringUtils.isNullOrEmpty(configuration.getS3().getBucketName())) {
					log.error(
							"Bucket-name cannot be left empty under property 'com.behl.strongbox.aws.s3.bucket-name'");
					exit();
				}
				if (!StringUtils.isNullOrEmpty(configuration.getAccessKey())
						&& !StringUtils.isNullOrEmpty(configuration.getSecretAccessKey())) {
					if (StringUtils.isNullOrEmpty(configuration.getS3().getRegion())) {
						log.error("A valid region must be provided corresponding to configured S3 Bucket");
						exit();
					}
				}
			}

		}

		public void validate(final AzureConfigurationProperties configuration) {
			if (BooleanUtils.isTrue(configuration.getEnabled())) {
				if (StringUtils.isNullOrEmpty(configuration.getContainer())) {
					log.error(
							"container name cannot be left empty under property 'com.behl.strongbox.azure.container'");
					exit();
				}
				if (StringUtils.isNullOrEmpty(configuration.getConnectionString())
						&& (StringUtils.isNullOrEmpty(configuration.getSasToken())
								&& StringUtils.isNullOrEmpty(configuration.getSasUrl()))
						|| (StringUtils.isNullOrEmpty(configuration.getConnectionString())
								&& (StringUtils.isNullOrEmpty(configuration.getSasUrl())
										|| StringUtils.isNullOrEmpty(configuration.getSasToken())))) {
					log.error(
							"Either a connection string or a combination of SAS Token and SAS URL have to be configured to enable connection to configured container");
					exit();
				}
			}
		}

		public void validate(final GcpStorageConfigurationProperties configuration) {
			if (BooleanUtils.isTrue(configuration.getEnabled())) {
				if (StringUtils.isNullOrEmpty(configuration.getBucketName())
						|| StringUtils.isNullOrEmpty(configuration.getAuthenticationKeyPath())
						|| StringUtils.isNullOrEmpty(configuration.getProjectId())) {
					log.error(
							"All configuration values must be present for GCP Storage under 'com.behl.strongbox.gcp.*' in application.properties file. Refer {}",
							GcpStorageConfigurationProperties.class.getName());
					exit();
				}
			}
		}

		public void validate(final DigitalOceanSpacesConfigurationProperties configuration) {
			if (BooleanUtils.isTrue(configuration.getEnabled())) {
				if (StringUtils.isNullOrEmpty(configuration.getAccessKey())
						|| StringUtils.isNullOrEmpty(configuration.getSecretKey())
						|| StringUtils.isNullOrEmpty(configuration.getBucketName())
						|| StringUtils.isNullOrEmpty(configuration.getEndpoint())
						|| StringUtils.isNullOrEmpty(configuration.getRegion())) {
					log.error(
							"All configuration values must be present for Digital Ocean Storage under 'com.behl.strongbox.digital-ocean.spaces.*' in application.properties file. Refer {}",
							DigitalOceanSpacesConfigurationProperties.class.getName());
					exit();
				}
			}
		}

		public void validate(final WasabiConfigurationProperties configuration) {
			if (BooleanUtils.isTrue(configuration.getEnabled())) {
				if (StringUtils.isNullOrEmpty(configuration.getAccessKey())
						|| StringUtils.isNullOrEmpty(configuration.getSecretKey())
						|| StringUtils.isNullOrEmpty(configuration.getBucketName())
						|| StringUtils.isNullOrEmpty(configuration.getRegion())) {
					log.error(
							"All configuration values must be present for Digital Ocean Storage under 'com.behl.strongbox.wasabi.*' in application.properties file. Refer {}",
							WasabiConfigurationProperties.class.getName());
					exit();
				}
			}
		}

		public void validate(final S3NinjaConfigurationProperties configuration) {
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

		/**
		 * Returns true if none of the storage service integrations are configured
		 */
		public boolean allDisabled(final AwsConfigurationProperties awsConfigurationProperties,
				final AzureConfigurationProperties azureConfigurationProperties,
				final GcpStorageConfigurationProperties gcpStorageConfigurationProperties,
				final DigitalOceanSpacesConfigurationProperties digitalOceanSpacesConfigurationProperties,
				final WasabiConfigurationProperties wasabiConfigurationProperties,
				final S3NinjaConfigurationProperties s3NinjaConfigurationProperties) {
			return (awsConfigurationProperties.getEnabled() == null
					|| BooleanUtils.isFalse(awsConfigurationProperties.getEnabled()))
					&& (azureConfigurationProperties.getEnabled() == null
							|| BooleanUtils.isFalse(azureConfigurationProperties.getEnabled()))
					&& (gcpStorageConfigurationProperties.getEnabled() == null
							|| BooleanUtils.isFalse(gcpStorageConfigurationProperties.getEnabled()))
					&& (digitalOceanSpacesConfigurationProperties.getEnabled() == null
							|| BooleanUtils.isFalse(digitalOceanSpacesConfigurationProperties.getEnabled()))
					&& (wasabiConfigurationProperties.getEnabled() == null
							|| BooleanUtils.isFalse(wasabiConfigurationProperties.getEnabled()))
					&& (s3NinjaConfigurationProperties.getEnabled() == null
							|| BooleanUtils.isFalse(s3NinjaConfigurationProperties.getEnabled()));
		}

	}

	private static void exit() {
		System.exit(0);
	}

}
