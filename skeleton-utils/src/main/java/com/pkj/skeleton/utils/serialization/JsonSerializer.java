package com.pkj.skeleton.utils.serialization;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonSerializer {

	private static ObjectMapper mapper = new ObjectMapper();
	private static Logger logger = Logger.getLogger(JsonSerializer.class);

	public static String serialize(Object o) {
		if (o == null) {
			throw new NullPointerException("Object passed for serialization is null!");
		}
		try {
			return mapper.writeValueAsString(o);
		} catch (IOException e) {
			logger.error("Error while serializing the object to json.", e);
			throw new RuntimeException(e);
		}
	}

}
