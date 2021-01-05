package com.hien.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Strings;

public class JsonSerializer {
	private static final ObjectMapper mapper;
	private static final ObjectMapper prettyPrintMapper;

	static {
		mapper = initMapper();

		prettyPrintMapper = initMapper();
		prettyPrintMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	private static ObjectMapper initMapper() {
		ObjectMapper mapper = new ObjectMapper();

		mapper.setConfig(mapper.getSerializationConfig().withView(Object.class));

		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.ANY)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.registerModule(new GuavaModule());
		mapper.registerModule(new Jdk8Module());
		mapper.registerModule(new JavaTimeModule());

		return mapper;
	}

	public static boolean isJSONValid(String jsonInString) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			mapper.readTree(jsonInString);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static <T> T json2Object(String json, Class<T> tClass) {
		try {
			T obj = null;
			if (!Strings.isNullOrEmpty(json)) {
				obj = mapper.readValue(json, tClass);
			}

			return obj;
		} catch (IOException ex) {
			throw new RuntimeException("Unable to parse: " + json, ex);
		}
	}

	public static <T> List<T> json2List(String json, Class<T> classType) {
		try {
			List<T> listObj = new ArrayList<>();
			if (!Strings.isNullOrEmpty(json)) {
				listObj = mapper.readValue(json,
						TypeFactory.defaultInstance()
								.constructCollectionType(ArrayList.class, classType));
			}

			return listObj;
		} catch (IOException ex) {
			throw new RuntimeException("Unable to parse: " + json, ex);
		}
	}

	public static <T> Map<String, T> json2Map(String json, Class<T> classType) {
		try {
			Map<String, T> result = new HashMap<>();
			if (!Strings.isNullOrEmpty(json)) {
				return mapper.readValue(json, TypeFactory.defaultInstance()
						.constructParametricType(HashMap.class,
								String.class,
								classType));
			}

			return result;
		} catch (IOException ex) {
			throw new RuntimeException("Unable to parse: " + json, ex);
		}
	}

	public static String object2Json(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException ex) {
			throw new RuntimeException("Unable to serialize " + obj.getClass().getName() + ": " + obj.toString(), ex);
		}
	}

	public static String prettyPrintObject2Json(Object obj) {
		try {
			return prettyPrintMapper.writeValueAsString(obj);
		} catch (JsonProcessingException ex) {
			throw new RuntimeException("Unable to serialize " + obj.getClass().getName() + ": " + obj.toString(), ex);
		}
	}

	public static JsonNode object2JsonNode(Object value) {
		return mapper.valueToTree(value);
	}

	public static <T> T convertValue(Object copySource, Class<T> destinationClass) {
		return mapper.convertValue(copySource, destinationClass);
	}

	public static <T> T deepClone(Object copySource, Class<T> destinationClass) {
		String json = object2Json(copySource);
		return json2Object(json, destinationClass);
	}

	public static <T> Map<String, T> deepClone2Map(Object copySource, Class<T> destinationClass) {
		String json = object2Json(copySource);
		return json2Map(json, destinationClass);
	}

	public static <T> List<T> deepClone2List(Object copySource, Class<T> destinationClass) {
		String json = object2Json(copySource);
		return json2List(json, destinationClass);
	}

	public static Map<String, Object> deepMerge(Map<String, Object> oldMap, Map<String, Object> newMap) {
		for (String key : newMap.keySet()) {
			if (newMap.get(key) != null) {
				if (newMap.get(key) instanceof Map && oldMap.get(key) instanceof Map && oldMap.get(key) != null) {
					Map<String, Object> oldChild = (Map<String, Object>) oldMap.get(key);
					Map<String, Object> newChild = (Map<String, Object>) newMap.get(key);
					oldMap.put(key, deepMerge(oldChild, newChild));
				} else {
					oldMap.put(key, newMap.get(key));
				}
			}
		}
		return oldMap;
	}

	public static <T> T deepMerge(T o, T n, Class<T> clazz) {
		Map<String, Object> oldMap = deepClone2Map(o, Object.class);
		Map<String, Object> newMap = deepClone2Map(n, Object.class);
		newMap = deepMerge(oldMap, newMap);
		return deepClone(newMap, clazz);
	}

	public static Map deepMerge2Map(Object o, Object n) {
		Map<String, Object> oldMap = deepClone2Map(o, Object.class);
		Map<String, Object> newMap = deepClone2Map(n, Object.class);
		newMap = deepMerge(oldMap, newMap);
		return newMap;
	}
}
