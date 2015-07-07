package com.gunsoutsoftware.squabble.server.bootstrap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gunsoutsoftware.squabble.SquabblePublisher;
import com.gunsoutsoftware.squabble.SquabbleResource;
import com.gunsoutsoftware.squabble.aws.AwsSqsPublisher;
import com.gunsoutsoftware.squabble.aws.DefaultAwsSqsQueueUrlProvider;
import com.gunsoutsoftware.squabble.serde.JsonSquabbleEventSerializer;


@Configuration
@Profile("squabble-default")
public class DefaultConfig {

	@Bean
	ObjectMapper objectMapper() {
		// TODO: config this guy
		return new ObjectMapper();
	}
	
	@Bean
	JsonSquabbleEventSerializer jsonSquabbleEventSerializer() {
		return new JsonSquabbleEventSerializer(objectMapper());
	}
	
	@Bean
	AWSCredentialsProviderChain awsCredentialsProviderChain() {
		// pull aws creds from env variables or for a profile file
		AWSCredentialsProvider[] providers = {
			// AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY
			new EnvironmentVariableCredentialsProvider(),
			// ~/.aws/credentials
			new ProfileCredentialsProvider()
		};
		// build the chain and return the creds
		return new AWSCredentialsProviderChain(providers);
	}
	
	@Bean
	AmazonSQSClient amazonSQSClient() {
		return new AmazonSQSClient(awsCredentialsProviderChain());
	}
	
	@Bean
	DefaultAwsSqsQueueUrlProvider defaultAwsSqsQueueUrlProvider() {
		return new DefaultAwsSqsQueueUrlProvider(amazonSQSClient());
	}	
	
	@Bean
	SquabblePublisher awsSquabblePublisher() {
		return new AwsSqsPublisher(amazonSQSClient(), defaultAwsSqsQueueUrlProvider(), jsonSquabbleEventSerializer());
	}
	
	@Bean
	SquabbleResource squabbleResource() {
		return new SquabbleResource(awsSquabblePublisher());
	}
	
}
