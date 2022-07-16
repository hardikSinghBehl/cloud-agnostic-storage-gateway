package com.behl.strongbox.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.StringUtils;
import com.behl.strongbox.configuration.properties.AwsConfigurationProperties;
import com.behl.strongbox.configuration.properties.DigitalOceanSpacesConfigurationProperties;
import com.behl.strongbox.configuration.properties.S3NinjaConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(value = { AwsConfigurationProperties.class, S3NinjaConfigurationProperties.class,
		DigitalOceanSpacesConfigurationProperties.class })
public class AwsS3Configuration {

	private final AwsConfigurationProperties awsConfigurationProperties;
	private final S3NinjaConfigurationProperties s3NinjaConfigurationProperties;
	private final DigitalOceanSpacesConfigurationProperties digitalOceanSpacesConfigurationProperties;

	@Bean("amazonS3")
	@Primary
	public AmazonS3 amazonS3() {
		if (Boolean.TRUE.equals(awsConfigurationProperties.getEnabled())) {
			if (!StringUtils.isNullOrEmpty(awsConfigurationProperties.getAccessKey())
					&& !StringUtils.isNullOrEmpty(awsConfigurationProperties.getSecretAccessKey())
					&& !StringUtils.isNullOrEmpty(awsConfigurationProperties.getS3().getRegion())) {
				validateRegion(awsConfigurationProperties.getS3().getRegion());
				AWSCredentials awsCredentials = new BasicAWSCredentials(awsConfigurationProperties.getAccessKey(),
						awsConfigurationProperties.getSecretAccessKey());
				return AmazonS3ClientBuilder.standard()
						.withRegion(Regions.fromName(awsConfigurationProperties.getS3().getRegion()))
						.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
			} else {
				return AmazonS3ClientBuilder.standard().withRegion(Regions.getCurrentRegion().getName())
						.withCredentials(new DefaultAWSCredentialsProviderChain()).build();
			}
		}
		return null;
	}

	@Bean("emulatedAmazonS3")
	public AmazonS3 emulatedAmazonS3() {
		if (Boolean.TRUE.equals(s3NinjaConfigurationProperties.getEnabled())) {
			var endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
					s3NinjaConfigurationProperties.getS3().getEndpoint(), Regions.AP_SOUTH_1.getName());
			var awsCredentials = new BasicAWSCredentials(s3NinjaConfigurationProperties.getAccessKey(),
					s3NinjaConfigurationProperties.getSecretAccessKey());
			return AmazonS3ClientBuilder.standard().withEndpointConfiguration(endpointConfiguration)
					.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withPathStyleAccessEnabled(true)
					.build();
		}
		return null;

	}

	@Bean("digitalOceanSpace")
	public AmazonS3 digitalOceanSpace() {
		if (Boolean.TRUE.equals(digitalOceanSpacesConfigurationProperties.getEnabled())) {
			var endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
					digitalOceanSpacesConfigurationProperties.getEndpoint(), Regions.AP_SOUTH_1.getName());
			var awsCredentials = new BasicAWSCredentials(digitalOceanSpacesConfigurationProperties.getAccessKey(),
					digitalOceanSpacesConfigurationProperties.getSecretKey());
			return AmazonS3ClientBuilder.standard().withEndpointConfiguration(endpointConfiguration)
					.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
		}
		return null;

	}

	private void validateRegion(final String region) {
		try {
			Regions.fromName(region);
		} catch (final IllegalArgumentException exception) {
			log.error("A valid region must be provided corresponding to configured S3 Bucket");
			System.exit(0);
		}
	}

}