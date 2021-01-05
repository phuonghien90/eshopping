package com.hien.base;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class CustomImmutableMapBuilder<K, V> extends ImmutableMap.Builder<K, V> {
	public static ImmutableMap<String, Object> of(Object... keyValuePairs) {
		if (keyValuePairs == null || keyValuePairs.length == 0) {
			return ImmutableMap.of();
		}

		if (keyValuePairs.length % 2 != 0) {
			throw new IllegalArgumentException("Wrong number of arguments");
		}

		CustomImmutableMapBuilder<String, Object> builder = new CustomImmutableMapBuilder<>();
		for (int i = 0; i < keyValuePairs.length; i += 2) {
			builder.putNotNull((String) keyValuePairs[i], keyValuePairs[i + 1]);
		}

		return builder.build();
	}

	public static ImmutableMap<String, Object> ofWithDefault(Object... keyValuePairs) {
		if (keyValuePairs == null || keyValuePairs.length == 0) {
			return ImmutableMap.of();
		}

		if (keyValuePairs.length % 3 != 0) {
			throw new IllegalArgumentException("Wrong number of arguments");
		}

		CustomImmutableMapBuilder<String, Object> builder = new CustomImmutableMapBuilder<>();
		for (int i = 0; i < keyValuePairs.length; i += 3) {
			builder.putWithDefault((String) keyValuePairs[i], keyValuePairs[i + 1], keyValuePairs[i + 2]);
		}

		return builder.build();
	}

	public static <T> ImmutableMap<String, T> ofWithType(Class<T> clazz, Object... keyValuePairs) {
		if (keyValuePairs == null || keyValuePairs.length == 0 || clazz == null) {
			return ImmutableMap.of();
		}

		if (keyValuePairs.length % 2 != 0) {
			throw new IllegalArgumentException("Wrong number of arguments");
		}

		CustomImmutableMapBuilder<String, Object> builder = new CustomImmutableMapBuilder<>();
		for (int i = 0; i < keyValuePairs.length; i += 2) {
			builder.putNotNull((String) keyValuePairs[i], keyValuePairs[i + 1]);
		}

		return (ImmutableMap<String, T>) builder.build();
	}

	public CustomImmutableMapBuilder<K, V> putNotNull(K key, V value) {
		if (key != null && value != null) {
			this.put(key, value);
		}

		return this;
	}

	@Override
	public CustomImmutableMapBuilder<K, V> put(K key, V value) {
		super.put(key, value);
		return this;
	}

	public CustomImmutableMapBuilder<K, V> putWithDefault(K key, V value, V defaultValue) {
		V actualValue = value;
		if (actualValue == null) {
			actualValue = defaultValue;
		}

		this.put(key, actualValue);
		return this;
	}

	public CustomImmutableMapBuilder<K, V> putAllNotNull(Map<? extends K, ? extends V> map) {
		for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
			if (entry.getKey() != null && entry.getValue() != null) {
				this.put(entry.getKey(), entry.getValue());
			}
		}

		return this;
	}
}
