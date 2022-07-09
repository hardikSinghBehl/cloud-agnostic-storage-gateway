package com.behl.strongbox.utility;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.behl.strongbox.configuration.properties.AwsConfigurationProperties;
import com.behl.strongbox.configuration.properties.AzureConfigurationProperties;
import com.behl.strongbox.constant.Platform;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(value = { AwsConfigurationProperties.class, AzureConfigurationProperties.class })
public class PlatformUtility {

	private final AwsConfigurationProperties awsConfigurationProperties;
	private final AzureConfigurationProperties azureConfigurationProperties;

	public void validateIfEnabled(@NonNull final Platform platform) {
		if (!isEnabled(platform)) {
			log.error(
					"SELECTED PLATFORM {} IS NOT ENABLED OR CONFIGURED PROPERLY. GO TO 'appication.properties' FILE TO UPDATE CONFIGURATION",
					platform.name());
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
					"Selected Platform '" + platform.name() + "' is not configured");
		}
	}

	private boolean isEnabled(@NonNull final Platform platform) {
		if (Platform.AWS.equals(platform))
			return BooleanUtils.isTrue(awsConfigurationProperties.getEnabled());
		return BooleanUtils.isTrue(azureConfigurationProperties.getEnabled());
	}

}