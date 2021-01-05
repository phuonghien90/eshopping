package com.hien.base.log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.hien.base.JsonSerializer;

@Plugin(name = "Logstash",
		category = Core.CATEGORY_NAME,
		elementType = Appender.ELEMENT_TYPE,
		printObject = true)
public class LogstashAppender extends AbstractAppender {

	private static final long serialVersionUID = 1L;
	private static final String TRUNCATED_BY_LOGSTASH_APPENDER = "...[truncated by logstash appender]";
	private static final int TRUNCATE_MSG_LENGTH = TRUNCATED_BY_LOGSTASH_APPENDER.length();

	private final String application;
	private final int logstashPort;
	private final int stacktraceLength;
	private final InetAddress address;
	private final DatagramSocket socket;

	public LogstashAppender(String name,
			Filter filter,
			Layout<? extends Serializable> layout,
			String application,
			String logstashHost,
			int logstashPort,
			int stacktraceLength) {

		super(name, filter, layout, true);
		this.application = application;
		this.logstashPort = logstashPort;
		this.stacktraceLength = stacktraceLength;
		this.address = getAddress(logstashHost);
		this.socket = getSocket();
	}

	private DatagramSocket getSocket() {
		try {
			return new DatagramSocket();
		} catch (SocketException e) {
			LOGGER.error("Could not create UDP socket");
			return null;
		}
	}

	private InetAddress getAddress(String logstashHost) {
		try {
			return InetAddress.getByName(logstashHost);
		} catch (UnknownHostException e) {
			LOGGER.error("Could not find host: " + logstashHost);
			return null;
		}
	}

	@PluginFactory
	public static LogstashAppender createAppender(
			@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") Filter filter,
			@PluginAttribute("application") String application,
			@PluginAttribute("logstashHost") String logstashHost,
			@PluginAttribute("logstashPort") String logstashPortString,
			@PluginAttribute("parameters") String parameters,
			@PluginAttribute("stacktraceLength") String stacktraceLengthString) {

		if (name == null) {
			LOGGER.error("No name provided for LogstashAppender");
			return null;
		}

		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}

		int logstashPort = 0;
		try {
			logstashPort = Integer.parseInt(logstashPortString);
		} catch (NumberFormatException e) {
			LOGGER.error("logstashPort must be an integer value");
			return null;
		}

		if (parameters == null) {
			parameters = "";
		}

		int stacktraceLength = Integer.MIN_VALUE;
		if (stacktraceLengthString != null) {
			try {
				stacktraceLength = Integer.parseInt(stacktraceLengthString);
			} catch (NumberFormatException e) {
				LOGGER.error("stacktraceLength must be an integer value");
				return null;
			}
		}

		return new LogstashAppender(
				name,
				filter,
				layout,
				application,
				logstashHost,
				logstashPort,
				stacktraceLength);
	}

	@Override
	public void append(LogEvent event) {
		if (socket == null) {
			return;
		}

		String data = getData(event);
		byte[] buffer = data.getBytes(Charset.forName("UTF-8"));

		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, logstashPort);
		try {
			socket.send(packet);
		} catch (IOException e) {
			System.err.println("Could not send UDP packet");
		}
	}

	private String getData(LogEvent event) {
		Map<String, Object> data = new LinkedHashMap<>();
		String msg = event.getMessage().getFormattedMessage();
		if (JsonSerializer.isJSONValid(msg)) {
			Map<String, Object> d = JsonSerializer.json2Map(msg, Object.class);
			data.putAll(flatten(d));
		} else {
			data.put("message", msg);
		}
		data.put("name", event.getLoggerName());
		data.put("level", event.getLevel().getStandardLevel());

		if (application != null) {
			data.put("application", application);
		}

		StackTraceElement source = event.getSource();
		if (source != null) {
			data.put("className", source.getClassName());
			data.put("lineNumber", source.getLineNumber());
		}

		Throwable thrown = event.getThrown();
		if (thrown != null) {
			data.put("stacktrace", getStacktrace(thrown));
		}

		return JsonSerializer.object2Json(data);
	}

	public static Stream<AbstractMap.SimpleEntry> flatten(String path, Object o) {
		if (o instanceof Map<?, ?>) {
			return ((Map<?, ?>) o).entrySet().stream()
					.map(e -> new AbstractMap.SimpleEntry<>(path + "." + e.getKey(), e.getValue()))
					.flatMap(e -> LogstashAppender.flatten(e.getKey(), e.getValue()));
		} else if (o instanceof List<?>) {
			Stream.of(new AbstractMap.SimpleEntry<>(path, JsonSerializer.prettyPrintObject2Json(o)));
		}
		return Stream.of(new AbstractMap.SimpleEntry<>(path, o));
	}

	public static Map<String, Object> flatten(Map<String, Object> map) {
		return map.entrySet().stream()
				.flatMap(e -> LogstashAppender.flatten(e.getKey(), e.getValue()))
				.collect(Collectors.toMap(e -> (String) ((AbstractMap.SimpleEntry) e).getKey(), e -> ((AbstractMap.SimpleEntry) e).getValue()));
	}

	private String getStacktrace(Throwable throwable) {
		try (StringWriter stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter)) {
			throwable.printStackTrace(printWriter);
			String stackTrace = stringWriter.toString();
			if (stacktraceLength >= 0) {
				return stackTrace.substring(0, Math.min(stackTrace.length(), stacktraceLength) - TRUNCATE_MSG_LENGTH) + TRUNCATED_BY_LOGSTASH_APPENDER;
			}
			return stackTrace;
		} catch (IOException e) {
			System.err.println("Error when fetching stacktrace");
			return null;
		}
	}

	@Override
	public void stop() {
		super.stop();
		if (socket != null) {
			socket.close();
		}
	}

	static String escape(String s) {
		if (s == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		escape(s, sb);
		return sb.toString();
	}

	private static void escape(String s, StringBuffer sb) {
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch)
			{
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				// Reference: http://www.unicode.org/versions/Unicode5.1.0/
				if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}
	}
}