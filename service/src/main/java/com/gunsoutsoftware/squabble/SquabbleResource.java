package com.gunsoutsoftware.squabble;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.gunsoutsoftware.common.event.SquabbleEvent;

@Path("/{namespace}/{id}/{eventName}")
public class SquabbleResource {

	@Context
	private HttpHeaders headers;

	private SquabblePublisher publisher;
	
	public SquabbleResource(SquabblePublisher publisher) {
		Preconditions.checkNotNull(publisher);
		this.publisher = publisher;
	}

	@GET
	public Response ensureEndpoint(@PathParam("namespace") String namespace,
			@PathParam("id") String customerId,
			@PathParam("eventName") String eventName) {
		// some integrations use GET as a "validation" phase to ensure the given
		// endpoint is valid - let's support this pattern by default
		return Response.ok().build();
	}

	@POST
	public Response publish(@PathParam("namespace") String namespace,
			@PathParam("id") String eventId,
			@PathParam("eventName") String eventName, String body) {
		
		// translate the headers to a Multimap
		Multimap<String, String> translated = translate(headers.getRequestHeaders());
		// try to "publish" the event
		SquabbleEvent event = SquabbleEvent.newBuilder()
				.headers(translated)
				.eventId(eventId)
				.eventName(eventName)
				.namespace(namespace)
				.body(body)
				.build();
		// publish the event
		publisher.publish(event);
		// immediately respond with 202 accepted
		return Response.accepted().build();
	}
		
	@VisibleForTesting
	protected <K, V> Multimap<K, V> translate(MultivaluedMap<K, V> source) {
		Multimap<K, V> result = ArrayListMultimap.create(source.keySet().size(), 5);
		for (K key : source.keySet()) {
			result.putAll(key, source.get(key));
		}		
		return result;
	}

}
