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

	/**
	 * Name of container in Azure Storage account that will be used to store and
	 * retrieve files
	 */
	private String container;

	/**
	 * Connection String value required to enable connection to configured
	 * container. Either connection String or a combination of sasToken and sasUrl
	 * have to be configured to enable connection
	 */
	private String connectionString;

	/**
	 * SAS Token corresponding to configured SAS URL and container. Either a
	 * combination of sasToken and sasUrl or a connection String have to be
	 * configured to enable connection
	 */
	private String sasToken;

	/**
	 * SAS URL (Endpoint) corresponding to configured SAS Token and container.
	 * Either a combination of sasToken and sasUrl or a connection String have to be
	 * configured to enable connection
	 */
	private String sasUrl;

}
