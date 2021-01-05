package com.hien.eshopping.api.component.order.api.query;

import java.util.List;

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
import com.hien.base.exception.InvalidException;
import com.hien.base.log.LogObj;
import com.hien.base.mvc.CustomRestAPI;
import com.hien.eshopping.api.component.order.domain.Order;
import com.hien.eshopping.api.component.order.domain.OrderRepo;
import com.netflix.config.DynamicPropertyFactory;

@RestController
@RequestMapping(path = { "/api/v1/query/voucher" })
public class QueryVoucher {

	private static final String issuer = "eshopping";

	@Autowired
	private OrderRepo orderRepo;

	@PostMapping
	@CustomRestAPI
	public Object execute(@RequestHeader("token") String token,
			@RequestBody Request req,
			LogObj log) {

		String otp = "999"; // TODO: get otp from storage

		if (!req.otp.equals(otp)) {
			throw new InvalidException("otp");
		}

		Algorithm algorithm = Algorithm.HMAC256(secretKey());
		JWTVerifier verifier = JWT.require(algorithm)
				.withIssuer(issuer)
				.build();
		DecodedJWT jwt = verifier.verify(token);
		String phoneNumber = jwt.getClaim("phoneNumber").asString();

		List<Order> orders = orderRepo.findAll(OrderRepo.Spec.phoneNumber(phoneNumber));

		return orders;
	}

	private String secretKey() {
		return DynamicPropertyFactory.getInstance().getStringProperty("secure.secretKey", "hien123456").get();
	}

	public static class Request {
		public String otp;
	}
}
