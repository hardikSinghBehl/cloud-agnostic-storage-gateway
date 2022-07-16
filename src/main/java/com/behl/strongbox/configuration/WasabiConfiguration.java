package com.behl.strongbox.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.behl.strongbox.configuration.properties.WasabiConfigurationProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = WasabiConfigurationProperties.class)
public class WasabiConfiguration {

	private final WasabiConfigurationProperties wasabiConfigurationProperties;
	/**
	 * Wasabi Endpoint remains static irrespective of region and client being used,
	 * hence declaring it as a static class variable
	 */
	private static final String WASABI_ENDPOINT = "s3.wasabisys.com";

	@Bean("wasabiClient")
	public AmazonS3 wasabiClient() {
		if (Boolean.TRUE.equals(wasabiConfigurationProperties.getEnabled())) {
			var endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(WASABI_ENDPOINT,
					wasabiConfigurationProperties.getRegion());
			var awsCredentials = new BasicAWSCredentials(wasabiConfigurationProperties.getAccessKey(),
					wasabiConfigurationProperties.getSecretKey());
			return AmazonS3ClientBuilder.standard().withEndpointConfiguration(endpointConfiguration)
					.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
		}
		log.info("Wasabi Storage Integration Not Configured");
		return null;
	}
}