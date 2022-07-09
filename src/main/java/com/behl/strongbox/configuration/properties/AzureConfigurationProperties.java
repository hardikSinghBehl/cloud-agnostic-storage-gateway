package com.behl.strongbox.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "com.behl.strongbox.azure")
public class AzureConfigurationProperties {

	/**
	 * Boolean value on if Azure storage platform is to be configured as a storage
	 * integration service. Disabled by default if no value is provided
	 */
	private Boolean enabled;

}
