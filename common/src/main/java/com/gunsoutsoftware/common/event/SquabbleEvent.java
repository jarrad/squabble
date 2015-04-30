package com.gunsoutsoftware.common.event;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;

/**
 * Immutable representation of an externally invoked event.
 */
public class SquabbleEvent {

	public static final Builder newBuilder() {
		return new Builder();
	}
	
	private final String namespace;
	
	private final String id;
	
	private final String eventName;
	
	private final String body;
	
	private final DateTime receivedDate;
	
	private final Multimap<String, String> headers;
	
	private SquabbleEvent(Builder builder) {
		Preconditions.checkNotNull(builder);
		namespace = builder.namespace;
		id = builder.eventId;
		eventName = builder.eventName;
		body = builder.body;
		headers = builder.headers;
		receivedDate = DateTime.now(DateTimeZone.UTC);
	}
	
	public String getBody() {
		return body;
	}
	
	public String getId() {
		return id;
	}
	
	public String getEventName() {
		return eventName;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public Multimap<String, String> getHeaders() {
		return headers;
	}
	
	public DateTime getReceivedDate() {
		return receivedDate;
	}
	
	public static final class Builder {
		
		private String body;

		private String namespace;
		
		private String eventId;
		
		private String eventName;
		
		private Multimap<String, String> headers;
		
		Builder() {
		}
		
		public Builder body(String body) {
			this.body = body;
			return this;
		}
		
		public Builder namespace(String namespace) {
			this.namespace = namespace;
			return this;
		}

		public Builder eventId(String eventId) {
			this.eventId = eventId;
			return this;
		}

		public Builder eventName(String eventName) {
			this.eventName = eventName;
			return this;
		}
		
		public Builder headers(Multimap<String, String> headers) {
			this.headers = headers;
			return this;
		}
		
		public SquabbleEvent build() {
			return new SquabbleEvent(this);
		}
		
	}
	
}
