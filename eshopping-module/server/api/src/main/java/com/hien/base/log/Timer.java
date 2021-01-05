package com.hien.base.log;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Timer implements Serializable {

	private static final long serialVersionUID = -3620681826225735498L;
	@JsonIgnore
	private LocalDateTime start;
	@JsonIgnore
	private LocalDateTime end;
	private Long duration;

	public Timer() {
		this.start = LocalDateTime.now();
		this.duration = null;
	}

	public Timer(LocalDateTime defaultStartTime) {
		this.start = defaultStartTime;
		this.duration = null;
	}

	public void end() {
		this.end = LocalDateTime.now();
		this.duration = ChronoUnit.MILLIS.between(start, end);
	}

	public long getDuration() {
		if (duration == null) {
			this.end();
		}
		return this.duration;
	}

	@Override
	public String toString() {
		if (end != null && duration != null) {
			return String.format("[%s - %s] - %s mili",
					start.format(DateTimeFormatter.ofPattern("HH.mm:ss.SSS")),
					end.format(DateTimeFormatter.ofPattern("HH.mm:ss.SSS")),
					duration.toString()
					);
		} else {
			return "at " + start.format(DateTimeFormatter.ofPattern("HH.mm:ss.SSS"));
		}
	}
}
