package com.behl.strongbox.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.behl.strongbox.configuration.properties.DigitalOceanSpacesConfigurationProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = DigitalOceanSpacesConfigurationProperties.class)
public class DigitalOceanSpacesConfiguration {

	private final DigitalOceanSpacesConfigurationProperties digitalOceanSpacesConfigurationProperties;

	@Bean("digitalOceanSpace")
	public AmazonS3 digitalOceanSpace() {
		if (Boolean.TRUE.equals(digitalOceanSpacesConfigurationProperties.getEnabled())) {
			var endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
					digitalOceanSpacesConfigurationProperties.getEndpoint(),
					digitalOceanSpacesConfigurationProperties.getRegion());
			var awsCredentials = new BasicAWSCredentials(digitalOceanSpacesConfigurationProperties.getAccessKey(),
					digitalOceanSpacesConfigurationProperties.getSecretKey());
			return AmazonS3ClientBuilder.standard().withEndpointConfiguration(endpointConfiguration)
					.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
		}
		log.info("Digital Ocean Spaces Storage Integration Not Configured");
		return null;
	}
}
