package com.gunsoutsoftware.squabble.aws;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.google.common.base.Preconditions;
import com.gunsoutsoftware.common.event.SquabbleEvent;
import com.gunsoutsoftware.common.serde.Serializer;
import com.gunsoutsoftware.squabble.SquabblePublisher;

/**
 * {@link SquabblePublisher} which publishes each received {@link SquabbleEvent}
 * into SQS.
 * <p>
 * The given {@link AwsSqsQueueUrlProvider} is used to determine the Url of the
 * queue to publish the received event.
 * 
 * @see AwsSqsQueueUrlProvider
 */
public class AwsSqsPublisher implements SquabblePublisher {

	private static final Logger log = LoggerFactory
			.getLogger(AwsSqsPublisher.class);

	private final AmazonSQSClient sqs;

	private final Serializer<SquabbleEvent, String> serializer;

	private final AwsSqsQueueUrlProvider queueUrlProvider;

	public AwsSqsPublisher(AmazonSQSClient sqs,
			AwsSqsQueueUrlProvider queueUrlProvider,
			Serializer<SquabbleEvent, String> serializer) {
		Preconditions.checkNotNull(sqs);
		Preconditions.checkNotNull(queueUrlProvider);
		Preconditions.checkNotNull(serializer);

		this.serializer = serializer;
		this.queueUrlProvider = queueUrlProvider;
		this.sqs = sqs;
	}

	public void publish(SquabbleEvent event) {
		// serialize the event
		String message = null;
		
		try {
			message = serializer.serialize(event);
		} catch (IOException e) {
			log.error("Failed to serialize event: event={}", event, e);
		}

		// get the queueName
		String queueUrl = null;

		try {
			queueUrl = queueUrlProvider.getQueueUrl(event);
		} catch (QueueDoesNotExistException e) {
			log.error(
					"Cannot publish message. Queue not available: queueUrl={}, message={}",
					queueUrl, message);
			return;
		} catch (AmazonServiceException e) {
			log.error(
					"Failed to lookup the queue url. message={}, errorMessage={}, httpStatusCode={}, awsErrorCode={}, errorType={}, requestId={}",
					message, e.getErrorMessage(), e.getStatusCode(),
					e.getErrorCode(), e.getErrorType(), e.getRequestId(), e);
		}

		try {
			sqs.sendMessage(queueUrl, message);
		} catch (AmazonServiceException e) {
			// AWS rejected the message
			log.error(
					"AWS rejected message: queueUrl={}, message={}, errorMessage={}, httpStatusCode={}, awsErrorCode={}, errorType={}, requestId={}",
					queueUrl, message, e.getErrorMessage(), e.getStatusCode(),
					e.getErrorCode(), e.getErrorType(), e.getRequestId(), e);
		} catch (AmazonClientException e) {
			// client failed to send event
			log.error(
					"Problem connecting to AWQ/SQS: queueUrl={}, message={}, errorMessage={}",
					queueUrl, message, e.getMessage(), e);
		}
	}

}
