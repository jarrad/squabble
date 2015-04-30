package com.gunsoutsoftware.squabble.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.google.common.base.Joiner;
import com.gunsoutsoftware.common.event.SquabbleEvent;

/**
 * Builds the Sqs queue name using the namespace, id, and eventName joined by a
 * {@code "_"}.
 * <p>
 * e.g. Given this event
 * <pre>
 * <code>
 * {
 *   "namespace" : "greetings",
 *   "id": "1",
 *   "eventName" : "hello"
 * }
 * </code>
 * </pre>
 * 
 * the Sqs queue name would be: <code>greetings_1_hello</code>.
 * 
 */
public class DefaultAwsSqsQueueUrlProvider implements AwsSqsQueueUrlProvider {

	private static final Logger log = LoggerFactory
			.getLogger(DefaultAwsSqsQueueUrlProvider.class);

	private static final Joiner joiner = Joiner.on('_');

	private final AmazonSQS sqs;

	public DefaultAwsSqsQueueUrlProvider(AmazonSQSClient sqs) {
		this.sqs = sqs;
	}

	public String getQueueUrl(SquabbleEvent event)
			throws AmazonServiceException {
		// build the queue name
		final String queueName = joiner.join(event.getNamespace(),
				event.getId(), event.getEventName());

		log.trace("Built queueName from event: event={}, queueName={}", event,
				queueName);

		try {
			final GetQueueUrlResult result = sqs.getQueueUrl(queueName);
			return result.getQueueUrl();
		} catch (QueueDoesNotExistException e) {
			log.warn(
					"Queue does not exist: queueName={}, errorMessage={}, httpStatusCode={}, awsErrorCode={}, errorType={}, requestId={}",
					queueName, e.getErrorMessage(), e.getStatusCode(),
					e.getErrorCode(), e.getErrorType(), e.getRequestId(), e);
			
			CreateQueueRequest create = new CreateQueueRequest(queueName);
			return sqs.createQueue(create).getQueueUrl();
		}
	}

}
