package com.gunsoutsoftware.squabble;

import com.gunsoutsoftware.common.event.SquabbleEvent;

public interface SquabblePublisher {

	void publish(SquabbleEvent event);
	
}
