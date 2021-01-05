package com.hien.base.log;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.common.base.Strings;

public class Debug implements Serializable {

	private static final long serialVersionUID = 8004136985631038848L;

	private List<DebugEvent> debugEvents;

	public Debug() {
		this.debugEvents = new CopyOnWriteArrayList<>();
		this.debugEvents.add(new DebugEvent("start", 0));
	}

	public void flush() {
		this.debugEvents.clear();
	}

	public void debug(String desc) {
		if (desc == null) {
			desc = "null";
		}
		int debugEvtIndex = this.debugEvents.size();
		this.debugEvents.add(new DebugEvent(desc, debugEvtIndex));
	}

	public void debug(String key, String val) {
		if (key == null) {
			key = "null";
		}
		if (val == null) {
			val = "null";
		}

		this.debug(String.format("%s = %s", key, val));
	}

	public void error(Throwable tr) {
		this.debug(ExceptionUtils.getStackTrace(tr));
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		if (this.debugEvents.size() == 1) {
			str.append(String.format("Occurred at: %s",
					debugEvents.get(0).time.format(DateTimeFormatter.ISO_TIME)));
			return str.toString();
		}

		LocalDateTime start = this.debugEvents.get(0).time;
		LocalDateTime end = start;

		for (int i = 1; i < debugEvents.size(); i++) {
			DebugEvent evt = debugEvents.get(i);

			str.append(String.format("%s. %s ", i, Strings.nullToEmpty(evt.desc)));
			str.append(String.format("[timeStamp: %s - durationFromStart: %s]",
					evt.time.format(DateTimeFormatter.ISO_TIME),
					ChronoUnit.MILLIS.between(start, evt.time)));

			str.append("\n");
			end = evt.time;
		}

		str.append(String.format("### Total duration: %s", ChronoUnit.MILLIS.between(start, end)));

		return str.toString();
	}

	public LocalDateTime startTime() {
		if (this.debugEvents.size() > 0) {
			return this.debugEvents.get(0).time;
		} else {
			return LocalDateTime.now();
		}
	}
}
