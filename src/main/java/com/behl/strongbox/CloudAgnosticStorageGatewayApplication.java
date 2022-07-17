package com.behl.strongbox;

import java.time.LocalDateTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.behl.strongbox.configuration.properties.AwsConfigurationProperties;
import com.behl.strongbox.configuration.properties.AzureConfigurationProperties;
import com.behl.strongbox.configuration.properties.DigitalOceanSpacesConfigurationProperties;
import com.behl.strongbox.configuration.properties.GcpStorageConfigurationProperties;
import com.behl.strongbox.configuration.properties.S3NinjaConfigurationProperties;
import com.behl.strongbox.configuration.properties.WasabiConfigurationProperties;
import com.behl.strongbox.utility.BootstrapUtil;

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
		var digitalOceanConfigurationProperties = applicationContext
				.getBean(DigitalOceanSpacesConfigurationProperties.class);
		var wasabiConfigurationProperties = applicationContext.getBean(WasabiConfigurationProperties.class);
		var s3NinjaEmulatorConfigurationProperties = applicationContext.getBean(S3NinjaConfigurationProperties.class);

		final var validator = BootstrapUtil.getValidator();

		if (validator.allDisabled(awsConfigurationProperties, azureConfigurationProperties, gcpConfigurationProperties,
				digitalOceanConfigurationProperties, wasabiConfigurationProperties,
				s3NinjaEmulatorConfigurationProperties)) {
			log.error(
					"Atleast 1 Storage-service provider must be enabled : {} : Go to 'application.properties' file to update configuration",
					LocalDateTime.now());
			exit();
		}

		validator.validate(awsConfigurationProperties);
		validator.validate(azureConfigurationProperties);
		validator.validate(gcpConfigurationProperties);
		validator.validate(digitalOceanConfigurationProperties);
		validator.validate(wasabiConfigurationProperties);
		validator.validate(s3NinjaEmulatorConfigurationProperties);
	}

	private static void exit() {
		System.exit(0);
	}

}
