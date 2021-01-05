package com.hien.base.log;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DebugEvent implements Serializable {

	private static final long serialVersionUID = -8867127828971463325L;

	public final String desc;
	public final LocalDateTime time;
	public final int index;

	public DebugEvent(String desc, int index) {
		this.desc = String.format("%s (thread: %s)", desc, getThreadName());
		this.index = index;
		this.time = LocalDateTime.now();
	}

	public DebugEvent(DebugEvent that, int index) {
		this.desc = that.desc;
		this.index = index;
		this.time = that.time;
	}

	private String getThreadName() {
		return Thread.currentThread().getName();
	}

	public String getKey() {
		return desc.substring(0, desc.contains(" = ") ? desc.indexOf(" = ") : desc.length());
	}

	@Override
	public String toString() {
		return String.format("%s (%s)",
				desc, time.format(DateTimeFormatter.ISO_LOCAL_TIME));
	}
}
