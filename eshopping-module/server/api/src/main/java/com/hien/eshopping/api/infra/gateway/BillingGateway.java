package com.hien.eshopping.api.infra.gateway;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.hien.base.http.CustomHttpClient;
import com.hien.base.http.CustomHttpClient.Response;
import com.hien.base.log.LogObj;
import com.hien.eshopping.api.dto.BankCard;

@Service
public class BillingGateway {

	private static final String service = "billing";

	private static class path {
		private static final String verify = "/api/v1/purchase/verify";
		private static final String confirm = "/api/v1/purchase/confirm";
		private static final String commit = "/api/v1/purchase/commit";
		private static final String cancel = "/api/v1/purchase/cancel";
	}

	@Autowired
	private LoadBalancerClient loadBalancer;
	@Autowired
	private CustomHttpClient httpClient;

	public Long verifyPurchase(BankCard bankCard, BigDecimal price, LogObj log) {
		Map<String, Object> param = new LinkedHashMap<>();
		param.put("cardNumber", bankCard.number);
		param.put("expDate", bankCard.expDate);
		param.put("cvv", bankCard.cvv);
		param.put("amount", price);

		Response response = httpClient.fireHttpRequest(HttpMethod.POST, getUrl(path.verify), param, log);

		Long purchaseId = ((Number) response.responseOfMap(Object.class).get("purchaseId")).longValue();

		return purchaseId;
	}

	public void confirmPurchase(Long purchaseId, String otp, LogObj log) {
		Map<String, Object> param = new LinkedHashMap<>();
		param.put("purchaseId", purchaseId);
		param.put("otp", otp);

		httpClient.fireHttpRequest(HttpMethod.POST, getUrl(path.confirm), param, log);
	}

	public void commitPurchase(Long purchaseId, LogObj log) {
		Map<String, Object> param = new LinkedHashMap<>();
		param.put("purchaseId", purchaseId);

		httpClient.fireHttpRequest(HttpMethod.POST, getUrl(path.commit), param, log);
	}

	public void cancelPurchase(Long purchaseId, LogObj log) {
		Map<String, Object> param = new LinkedHashMap<>();
		param.put("purchaseId", purchaseId);

		httpClient.fireHttpRequest(HttpMethod.POST, getUrl(path.cancel), param, log);
	}

	private String getUrl(String path) {
		return loadBalancer.choose(service).getUri().toString() + path;
	}
}
