package com.behl.strongbox.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.StringUtils;
import com.behl.strongbox.configuration.properties.AwsConfigurationProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties(value = { AwsConfigurationProperties.class })
public class AwsS3Configuration {

	@Primary
	@Bean("amazonS3")
	public AmazonS3 amazonS3(final AwsConfigurationProperties awsConfigurationProperties) {
		AmazonS3 amazonS3 = null;
		if (Boolean.TRUE.equals(awsConfigurationProperties.getEnabled())) {
			if (!StringUtils.isNullOrEmpty(awsConfigurationProperties.getAccessKey())
					&& !StringUtils.isNullOrEmpty(awsConfigurationProperties.getSecretAccessKey())
					&& !StringUtils.isNullOrEmpty(awsConfigurationProperties.getS3().getRegion())) {
				validateRegion(awsConfigurationProperties.getS3().getRegion());
				AWSCredentials awsCredentials = new BasicAWSCredentials(awsConfigurationProperties.getAccessKey(),
						awsConfigurationProperties.getSecretAccessKey());
				amazonS3 = AmazonS3ClientBuilder.standard()
						.withRegion(Regions.fromName(awsConfigurationProperties.getS3().getRegion()))
						.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
			} else {
				amazonS3 = AmazonS3ClientBuilder.standard().withRegion(Regions.getCurrentRegion().getName())
						.withCredentials(new DefaultAWSCredentialsProviderChain()).build();
			}
			log.info("AWS S3 Storage Integration Configured Successfully");
			return amazonS3;
		}
		log.warn("AWS S3 Storage Integration Not Configured");
		return amazonS3;
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