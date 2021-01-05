package com.hien.base.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpMethod;

import com.hien.base.JsonSerializer;
import com.hien.base.exception.CustomException;
import com.hien.base.log.LogObj;

public class CustomHttpClient {

	private HttpClient httpClient;

	public CustomHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public Response fireHttpRequest(HttpMethod method, String url, Map<String, Object> body, LogObj log) {
		try {
			log.debug(constructDebugMessage(method.name(), url, body));
			HttpUriRequest request = constructHttpRequest(method, url, body);
			HttpResponse httpResponse = httpClient.execute(request);
			Response response = new Response(httpResponse);
			if (response.statusCode >= 500) {
				throw new RuntimeException(String.format("%s respond %s : %s", url, response.statusCode, response.body));
			}
			if (response.statusCode >= 400) {
				throw new CustomException((String) response.responseOfMap(Object.class).get("code"));
			}
			return response;
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Response fireHttpRequest(HttpUriRequest request, LogObj log) {
		try {
			log.debug(constructDebugMessage(request.getMethod(), request.getURI().toString(), null));
			HttpResponse httpResponse = httpClient.execute(request);
			Response response = new Response(httpResponse);
			if (response.statusCode >= 500) {
				throw new RuntimeException(String.format("%s respond %s : %s", request.getURI().toString(), response.statusCode, response.body));
			}
			if (response.statusCode >= 400) {
				throw new CustomException((String) response.responseOfMap(Object.class).get("code"));
			}
			return response;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Response fireHttpRequestNoCheckResponse(HttpUriRequest request, LogObj log) {
		try {
			log.debug(constructDebugMessage(request.getMethod(), request.getURI().toString(), null));
			HttpResponse httpResponse = httpClient.execute(request);
			Response response = new Response(httpResponse);
			return response;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private HttpUriRequest constructHttpRequest(HttpMethod method, String url, Map<String, Object> body) throws URISyntaxException, UnsupportedEncodingException {

		HttpUriRequest request;

		switch (method) {
		case POST:
			request = new HttpPost(url);
			break;
		case GET:
			request = new HttpGet(url);
			break;
		case PUT:
			request = new HttpPut(url);
			break;
		case DELETE:
			request = new HttpDelete(url);
			break;
		case PATCH:
			request = new HttpPatch(url);
			break;
		default:
			throw new RuntimeException("Unrecognized HTTP method");
		}

		request.setHeader("Accept", "application/json, text/plain, */*");
		request.setHeader("Content-type", "application/json;charset=UTF-8");
		request.setHeader("Connection", "keep-alive");
		request.setHeader("Accept-Encoding", "gzip,deflate,sdch");

		if (body != null) {
			if (EnumSet.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH).contains(method)) {
				((HttpEntityEnclosingRequest) request).setEntity(new StringEntity(JsonSerializer.object2Json(body)));
			}
			return request;
		}
		return request;
	}

	private String constructDebugMessage(String method, String url, Map<String, Object> body) {
		StringBuilder msg = new StringBuilder();

		msg.append(String.format("%s:%s", method, url));

		if (body != null && !body.isEmpty()) {
			Map flattenMap = new HashMap();
			flatten("", body, flattenMap);

			msg.append(String.format("\n\t%s: [%s]", "INPUT",
					body.keySet().stream()
							.map(k -> k + "=" + body.get(k)).collect(Collectors.joining("|"))));
		}

		return msg.toString();
	}

	private void flatten(String path, Object from, Map to) {
		if (to == null) {
			return;
		} else if (from instanceof Map && !((Map) from).isEmpty()) {
			((Map) from).entrySet().forEach(o -> {
				String pathPrefix = path.isEmpty() ? "" : path + ".";
				flatten(pathPrefix + ((Map.Entry) o).getKey(), ((Map.Entry) o).getValue(), to);
			});
		} else if ((from instanceof Collection) && !((Collection) from).isEmpty()) {
			List<Object> collection = Arrays.asList(((Collection) from).toArray());
			IntStream.range(0, collection.size()).forEach(i -> flatten(path + "[" + i + "]", collection.get(i), to));
		} else if (!StringUtils.isBlank(path)) {
			to.put(path, from);
		}
	}

	public class Response {

		private String body;
		private Map<String, String> headers;
		private int statusCode;
		private String reasonPhrase;

		public Response(HttpResponse response) throws IOException {
			body = EntityUtils.toString(response.getEntity());
			statusCode = response.getStatusLine().getStatusCode();
			reasonPhrase = response.getStatusLine().getReasonPhrase();
			headers = Arrays.asList(response.getAllHeaders())
					.stream()
					.collect(Collectors.toMap(n -> n.getName(), n -> n.getValue()));
		}

		public String getBody() {
			return body;
		}

		public Map<String, String> getHeaders() {
			return headers;
		}

		public int getStatusCode() {
			return statusCode;
		}

		public String getReasonPhrase() {
			return reasonPhrase;
		}

		public <T> Map<String, T> responseOfMap(Class<T> clazz) {
			return JsonSerializer.json2Map(body, clazz);
		}
	}
}
