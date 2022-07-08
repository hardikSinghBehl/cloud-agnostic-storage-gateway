package com.behl.strongbox.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.StringUtils;
import com.behl.strongbox.configuration.properties.AwsConfigurationProperties;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(value = AwsConfigurationProperties.class)
public class AwsS3Configuration {

	private final AwsConfigurationProperties awsConfigurationProperties;

	@Bean
	public AmazonS3 amazonS3() {
		if (Boolean.TRUE.equals(awsConfigurationProperties.getEnabled())) {
			if (!StringUtils.isNullOrEmpty(awsConfigurationProperties.getAccessKey())
					&& !StringUtils.isNullOrEmpty(awsConfigurationProperties.getSecretAccessKey())
					&& !StringUtils.isNullOrEmpty(awsConfigurationProperties.getS3().getRegion())) {
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

}