package net.doohad.models;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@SuppressWarnings("serial")
public class CustomObjectMapper extends ObjectMapper {
	public CustomObjectMapper() {
		super();
		configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
	}
}
