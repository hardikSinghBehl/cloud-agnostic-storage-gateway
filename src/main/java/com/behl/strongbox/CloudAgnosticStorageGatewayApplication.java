package com.behl.strongbox;

import java.time.LocalDateTime;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.behl.strongbox.configuration.properties.AwsConfigurationProperties;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class CloudAgnosticStorageGatewayApplication {

	public static void main(String[] args) {
		var applicationContext = SpringApplication.run(CloudAgnosticStorageGatewayApplication.class, args);

		var awsConfigurationProperties = applicationContext.getBean(AwsConfigurationProperties.class);

		if (allDisabled(awsConfigurationProperties)) {
			log.error(
					"Atleast 1 Storage-service provider must be enabled : {} : Go to 'application.properties' file to update configuration",
					LocalDateTime.now());
			exit();
		}
	}

	private static boolean allDisabled(final AwsConfigurationProperties awsConfigurationProperties) {
		return BooleanUtils.isFalse(awsConfigurationProperties.getEnabled());
	}

	private static void exit() {
		System.exit(0);
	}

}
