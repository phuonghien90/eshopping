package com.hien.eshopping.api.infra.gateway;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hien.base.JsonSerializer;
import com.hien.base.http.CustomHttpClient;
import com.hien.base.http.CustomHttpClient.Response;
import com.hien.base.log.LogObj;
import com.netflix.config.DynamicPropertyFactory;

@Service
public class TelecomGateway {

	private static final String issuer = "eshopping";

	private static final String telecomIssuer = "telecom";

	private static final String baseUrl = "http://hien-telecom:8080";

	private static final int generateVoucherTimeout = 120 * 1000;

	private static class path {
		private static final String generateVoucher = "/voucher/generate";
		private static final String destroyVoucher = "/voucher/destroy";
	}

	@Autowired
	private CustomHttpClient httpClient;

	public String generateVoucher(String skuCode, String phoneNumber, LogObj log) {
		try {
			String token = generateToken(skuCode, phoneNumber);

			Map<String, Object> body = new LinkedHashMap<>();
			body.put("skuCode", skuCode);
			body.put("phoneNumber", phoneNumber);

			HttpPost request = new HttpPost(getUrl(path.generateVoucher));
			request.setEntity(new StringEntity(JsonSerializer.object2Json(body)));

			request.setHeader("Content-type", "application/json;charset=UTF-8");
			request.setHeader("token", token);

			request.setConfig(RequestConfig.custom()
					.setConnectTimeout(generateVoucherTimeout)
					.setConnectionRequestTimeout(generateVoucherTimeout)
					.setSocketTimeout(generateVoucherTimeout)
					.build());

			Response response = httpClient.fireHttpRequestNoCheckResponse(request, log);

			if (response.getStatusCode() >= 300) {
				throw new RuntimeException("cannotGenerateVoucher");
			}
			if (!response.getHeaders().containsKey("token")) {
				throw new RuntimeException("cannotGenerateVoucher");
			}

			DecodedJWT resJwt = verifyToken(response.getHeaders().get("token"));

			String voucherInToken = resJwt.getClaim("voucher").asString();
			String voucherInRes = (String) response.responseOfMap(Object.class).get("voucher");

			if (!voucherInToken.equals(voucherInRes)) {
				throw new RuntimeException("cannotGenerateVoucher");
			}

			return voucherInRes;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private String generateToken(String skuCode, String phoneNumber) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey());

		String token = JWT.create()
				.withIssuer(issuer)
				.withClaim("skuCode", skuCode)
				.withClaim("phoneNumber", phoneNumber)
				.withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant()))
				.sign(algorithm);

		return token;
	}

	private DecodedJWT verifyToken(String token) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey());

		JWTVerifier verifier = JWT.require(algorithm)
				.withIssuer(telecomIssuer)
				.build();
		DecodedJWT jwt = verifier.verify(token);

		return jwt;
	}

	private String secretKey() {
		return DynamicPropertyFactory.getInstance().getStringProperty("secure.telecom.secretKey", "telecomSecretKey").get();
	}

	private String getUrl(String path) {
		return baseUrl + path;
	}
}
