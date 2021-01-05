package com.hien.base.log;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Metric implements Serializable {
	private static final long serialVersionUID = -7756058550130154998L;

	private Map<String, Object> data;

	public static class PredefinedCategory {
		public static String input = "input";
		public static String output = "output";
		public static String timer = "timer";

		static List<String> categories = Arrays.asList(input, output, timer);
	}

	public Metric() {
		this.data = Collections.synchronizedMap(new ConcurrentHashMap<>());

		this.data.put(PredefinedCategory.input, Collections.synchronizedMap(new ConcurrentHashMap<String, Object>()));
		this.data.put(PredefinedCategory.output, Collections.synchronizedMap(new ConcurrentHashMap<String, Object>()));
		this.data.put(PredefinedCategory.timer, new ConcurrentHashMap<String, Timer>());
	}

	public void start(String timer) {
		Map<String, Timer> timerMetric = (Map<String, Timer>) this.data.get(PredefinedCategory.timer);
		timerMetric.put(timer, new Timer());
	}

	public void end(String timer, LocalDateTime defaultStartTime) {
		Map<String, Timer> timerMetric = (Map<String, Timer>) this.data.get(PredefinedCategory.timer);

		if (timerMetric.get(timer) == null) {
			timerMetric.put(timer, new Timer(defaultStartTime));
		}

		timerMetric.get(timer).end();
	}

	public Map<String, Object> getRepresentedObject() {
		return this.data;
	}

	public void putGlobalMetric(String key, Object value) {
		if (value == null) {
			return;
		}
		this.data.put(key, value);
	}

	public void putInputMetric(String key, Object value) {
		if (value == null) {
			return;
		}
		((Map) this.data.get(PredefinedCategory.input)).put(key, value);
	}

	public void putOutputMetric(String key, Object value) {
		if (value == null) {
			return;
		}
		((Map) this.data.get(PredefinedCategory.output)).put(key, value);
	}
}
