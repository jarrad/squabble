package com.gunsoutsoftware.squabble.serde;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.gunsoutsoftware.common.event.SquabbleEvent;
import com.gunsoutsoftware.common.serde.Serializer;

public class JsonSquabbleEventSerializer implements
		Serializer<SquabbleEvent, String> {

	private static final Logger log = LoggerFactory
			.getLogger(JsonSquabbleEventSerializer.class);

	private final ObjectMapper mapper;

	public JsonSquabbleEventSerializer(ObjectMapper mapper) {
		Preconditions.checkNotNull(mapper);

		this.mapper = mapper;
	}

	public String serialize(SquabbleEvent object) throws IOException {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.warn("Failed to generate JSON for event: event={}", object, e);
			throw e;
		}
	}
}
