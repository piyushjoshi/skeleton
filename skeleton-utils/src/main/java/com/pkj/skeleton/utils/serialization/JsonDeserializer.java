package com.pkj.skeleton.utils.serialization;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonDeserializer {

	private static ObjectMapper mapper = new ObjectMapper();
	private static ObjectMapper mapperWithIgnoreUnknownProps = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	private static Logger logger = Logger.getLogger(JsonDeserializer.class);

	public static <T> T deserialize(String serializedForm, Class<T> type)
			throws JsonParseException, JsonMappingException, IOException {
		T deserializedObject = mapper.readValue(serializedForm, type);
		return deserializedObject;
	}

	public static <T> T deserializeWithIgnoreUnknownProps(String serializedForm, Class<T> type)
			throws JsonParseException, JsonMappingException, IOException {
		T deserializedObject = mapperWithIgnoreUnknownProps.readValue(serializedForm, type);
		return deserializedObject;
	}

}
