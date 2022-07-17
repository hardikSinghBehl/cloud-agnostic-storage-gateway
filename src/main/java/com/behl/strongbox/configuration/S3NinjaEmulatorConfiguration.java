package com.behl.strongbox.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.behl.strongbox.configuration.properties.S3NinjaConfigurationProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties(value = S3NinjaConfigurationProperties.class)
public class S3NinjaEmulatorConfiguration {

	@Bean("emulatedAmazonS3")
	public AmazonS3 emulatedAmazonS3(final S3NinjaConfigurationProperties s3NinjaConfigurationProperties) {
		if (Boolean.TRUE.equals(s3NinjaConfigurationProperties.getEnabled())) {
			var endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
					s3NinjaConfigurationProperties.getS3().getEndpoint(), Regions.AP_SOUTH_1.getName());
			var awsCredentials = new BasicAWSCredentials(s3NinjaConfigurationProperties.getAccessKey(),
					s3NinjaConfigurationProperties.getSecretAccessKey());
			var emulatedS3Client = AmazonS3ClientBuilder.standard().withEndpointConfiguration(endpointConfiguration)
					.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withPathStyleAccessEnabled(true)
					.build();
			log.info("S3 Ninja Emulation Storage Integration Configured Successfully");
			return emulatedS3Client;
		}
		log.warn("S3 Ninja Emulation Storage Integration Not Configured");
		return null;

	}

}