package com.hien.telecom.api.component.voucher.api;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.ImmutableMap;
import com.hien.base.exception.CustomException;
import com.hien.base.mvc.CustomRestAPI;
import com.hien.telecom.api.component.voucher.service.GenerateVoucherService;

@RestController
@RequestMapping(path = { "/voucher/generate" })
public class Generate {

	private static final String issuer = "telecom";

	private static final String eshoppingIssuer = "eshopping";

	@Autowired
	GenerateVoucherService generateVoucherService;

	@PostMapping
	@CustomRestAPI
	public Object execute(@RequestHeader("token") String token,
			@RequestBody Request req,
			HttpServletResponse response) {

		DecodedJWT jwt = verifyToken(token);

		if (!req.skuCode.equals(jwt.getClaim("skuCode").asString())) {
			throw new CustomException("invalidToken");
		}
		if (!req.phoneNumber.equals(jwt.getClaim("phoneNumber").asString())) {
			throw new CustomException("invalidToken");
		}

		String voucher = generateVoucherService.generateVoucher(req.skuCode, req.phoneNumber);

		response.addHeader("token", generateToken(voucher));

		return ImmutableMap.of("voucher", voucher);
	}

	private DecodedJWT verifyToken(String token) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey());

		JWTVerifier verifier = JWT.require(algorithm)
				.withIssuer(eshoppingIssuer)
				.build();
		DecodedJWT jwt = verifier.verify(token);

		return jwt;
	}

	private String generateToken(String voucher) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey());

		String token = JWT.create()
				.withIssuer(issuer)
				.withClaim("voucher", voucher)
				.withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant()))
				.sign(algorithm);

		return token;
	}

	private String secretKey() {
		return "telecomSecretKey";
	}

	public static class Request {
		public String skuCode;

		public String phoneNumber;
	}
}
