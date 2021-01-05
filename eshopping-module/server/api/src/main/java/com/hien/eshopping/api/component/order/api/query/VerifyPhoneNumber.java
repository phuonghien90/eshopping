package com.hien.eshopping.api.component.order.api.query;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.collect.ImmutableMap;
import com.hien.base.log.LogObj;
import com.hien.base.mvc.CustomRestAPI;
import com.hien.eshopping.api.component.order.service.SendSmsService;
import com.netflix.config.DynamicPropertyFactory;

@RestController
@RequestMapping(path = { "/api/v1/query/verify-phone-number" })
public class VerifyPhoneNumber {

	private static final String issuer = "eshopping";

	@Autowired
	SendSmsService smsService;

	@PostMapping
	@CustomRestAPI
	public Object execute(@RequestBody Request req,
			LogObj log) {

		String otp = "999"; // TODO: generate otp here
		
		smsService.sendOtpAsync(req.phoneNumber, otp);

		Algorithm algorithm = Algorithm.HMAC256(secretKey());
		String token = JWT.create()
				.withIssuer(issuer)
				.withClaim("phoneNumber", req.phoneNumber)
				.withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant()))
				.sign(algorithm);

		return ImmutableMap.of(
				"status", "ok",
				"token", token);
	}

	private String secretKey() {
		return DynamicPropertyFactory.getInstance().getStringProperty("secure.secretKey", "hien123456").get();
	}

	public static class Request {
		public String phoneNumber;
	}
}
