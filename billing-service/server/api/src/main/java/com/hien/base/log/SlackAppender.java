package com.hien.base.log;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
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

@Plugin(name = "Slack", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class SlackAppender extends AbstractAppender {

	private static final long serialVersionUID = 1L;
	private static final String TRUNCATED_BY_LOGSTASH_APPENDER = "...[truncated by slack appender]";
	private static final int TRUNCATE_MSG_LENGTH = TRUNCATED_BY_LOGSTASH_APPENDER.length();

	private final String application;
	private final String slackUrl;
	private final int stacktraceLength;
	private static CloseableHttpClient httpClient;

	public SlackAppender(String name,
			Filter filter,
			Layout<? extends Serializable> layout,
			String application,
			String slackUrl,
			int stacktraceLength) {

		super(name, filter, layout, true);
		this.application = application;
		this.slackUrl = slackUrl;
		this.stacktraceLength = stacktraceLength;
		this.httpClient = getHttpClient();
	}

	private CloseableHttpClient getHttpClient() {
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setMaxTotal(300);
		connManager.setDefaultMaxPerRoute(200);
		return HttpClients.custom()
				.setConnectionManager(connManager)
				.setDefaultRequestConfig(RequestConfig.custom()
						.setConnectionRequestTimeout(5 * 1000)
						.setConnectTimeout(5 * 1000)
						.setSocketTimeout(5 * 1000)
						.build())
				.setRedirectStrategy(new RedirectStrategy() {
					@Override
					public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) {
						return false;
					}

					@Override
					public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) {
						return null;
					}
				})
				.build();
	}

	@PluginFactory
	public static SlackAppender createAppender(
			@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") Filter filter,
			@PluginAttribute("application") String application,
			@PluginAttribute("slackUrl") String slackUrl,
			@PluginAttribute("parameters") String parameters,
			@PluginAttribute("stacktraceLength") String stacktraceLengthString) {

		if (name == null) {
			LOGGER.error("No name provided for LogstashAppender");
			return null;
		}

		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
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

		return new SlackAppender(
				name,
				filter,
				layout,
				application,
				slackUrl,
				stacktraceLength);
	}

	@Override
	public void append(LogEvent event) {
		if (httpClient == null) {
			return;
		}

		try {
			String data = getData(event);
			if (data.length() > stacktraceLength) {
				data = data.substring(0, stacktraceLength - TRUNCATE_MSG_LENGTH) + TRUNCATED_BY_LOGSTASH_APPENDER;
			}

			Map<String, String> body = new HashMap<>();
			body.put("text", String.format("```%s```", data));

			HttpPost request = new HttpPost(slackUrl);
			request.setHeader("Content-type", "application/json;charset=UTF-8");
			request.setEntity(new StringEntity(JsonSerializer.object2Json(body)));

			HttpResponse httpResponse = httpClient.execute(request);

			EntityUtils.consumeQuietly(httpResponse.getEntity());
		} catch (Throwable tr) {
			tr.printStackTrace();
		}
	}

	private String getData(LogEvent event) {
		String msg = event.getMessage().getFormattedMessage();
		if (JsonSerializer.isJSONValid(msg)) {
			try {
				Map<String, Object> d = JsonSerializer.json2Map(msg, Object.class);
				return (String) d.get("debug");
			} catch (Throwable tr) {
				return msg;
			}
		} else {
			return msg;
		}
	}

	@Override
	public void stop() {
		super.stop();
		if (httpClient != null) {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			switch (ch) {
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