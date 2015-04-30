package com.gunsoutsoftware.common.serde;

import java.io.IOException;

public interface Serializer<T, R> {

	R serialize(T object) throws IOException;
	
}
