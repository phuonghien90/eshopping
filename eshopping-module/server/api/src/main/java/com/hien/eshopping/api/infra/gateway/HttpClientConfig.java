package com.hien.eshopping.api.infra.gateway;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hien.base.http.CustomHttpClient;

@Configuration
public class HttpClientConfig {

	@Bean
	public CustomHttpClient httpClient() {
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setMaxTotal(300);
		connManager.setDefaultMaxPerRoute(200);
		HttpClient httpClient = HttpClients.custom()
				.setConnectionManager(connManager)
				.setDefaultRequestConfig(RequestConfig.custom()
						.setConnectionRequestTimeout(60 * 1000)
						.setConnectTimeout(60 * 1000)
						.setSocketTimeout(60 * 1000)
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

		return new CustomHttpClient(httpClient);
	}
}
