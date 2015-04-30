package com.gunsoutsoftware.squabble.aws;

import com.gunsoutsoftware.common.event.SquabbleEvent;

/**
 * 
 */
public interface AwsSqsQueueUrlProvider {

	/**
	 * Return the Url used to access the queue which should receive the given
	 * event.
	 * 
	 * @param event
	 *            the event to use as context for returning the SQS queue name
	 * @return the Url used to access the queue which should receive the given
	 *         event
	 */
	String getQueueUrl(SquabbleEvent event);

}
