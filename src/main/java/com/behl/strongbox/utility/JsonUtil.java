package com.behl.strongbox.utility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {

	private JsonUtil() {
	}

	public static Map<String, Object> toMap(final String jsonData) {
		if (StringUtils.isNullOrEmpty(jsonData))
			return null;
		final var objectMapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = objectMapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {
			});
		} catch (final IOException exception) {
			log.error("Exception occured while trying to parse JSON Data {} to Map.", jsonData, exception);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"INVALID JSON DATA PROVIDED IN FIELD customMetadata");
		}
		return map;
	}

}
