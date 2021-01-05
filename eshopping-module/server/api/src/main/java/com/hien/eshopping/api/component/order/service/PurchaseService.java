package com.hien.eshopping.api.component.order.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hien.base.log.LogObj;
import com.hien.eshopping.api.component.order.api.Purchase;
import com.hien.eshopping.api.component.order.domain.Order;
import com.hien.eshopping.api.component.order.domain.OrderRepo;
import com.hien.eshopping.api.component.product.domain.Product;
import com.hien.eshopping.api.infra.gateway.BillingGateway;
import com.hien.eshopping.api.infra.gateway.TelecomGateway;
import com.netflix.config.DynamicPropertyFactory;

@Service
public class PurchaseService {

	private static final String issuer = "eshopping";

	@Autowired
	OrderRepo orderRepo;
	@Autowired
	BillingGateway billingGateway;
	@Autowired
	TelecomGateway telecomGateway;

	public Order createOrder(Purchase.Request req) {
		Order order = new Order();
		order.phoneNumber = req.phoneNumber;
		order.skuCode = req.skuCode;
		order.status = Order.Status.pending;
		orderRepo.save(order);
		return order;
	}

	public void verifyPurchase(Purchase.Request req, Product product, Order order, LogObj log) {
		Long purchaseId = billingGateway.verifyPurchase(req.bankCard, product.price, log);
		order.purchaseId = purchaseId;
		order.status = Order.Status.confirming;
		orderRepo.save(order);
	}

	public String generateToken(Order order) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey());
		String token = JWT.create()
				.withIssuer(issuer)
				.withClaim("orderId", order.id)
				.withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant()))
				.sign(algorithm);
		return token;
	}

	public Order verifyToken(String token) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey());
		JWTVerifier verifier = JWT.require(algorithm)
				.withIssuer(issuer)
				.build();
		DecodedJWT jwt = verifier.verify(token);
		Long orderId = jwt.getClaim("orderId").asLong();
		Order order = orderRepo.findOne(orderId);
		return order;
	}

	public void confirmPurchase(Order order, String otp, LogObj log) {
		billingGateway.confirmPurchase(order.purchaseId, otp, log);
		order.status = Order.Status.reserved;
		orderRepo.save(order);
	}

	public void generateVoucherCode(Order order, LogObj log) {
		String voucherCode = telecomGateway.generateVoucher(order.skuCode, order.phoneNumber, log);
		order.voucherCode = voucherCode;
		order.status = Order.Status.gotVoucher;
		orderRepo.save(order);
	}

	public void commitPurchase(Order order, LogObj log) {
		billingGateway.commitPurchase(order.purchaseId, log);
		order.status = Order.Status.done;
		orderRepo.save(order);
	}
	
	public void cancelPurchase(Order order, LogObj log) {
		billingGateway.cancelPurchase(order.purchaseId, log);
		order.status = Order.Status.rejected;
		orderRepo.save(order);
	}

	private String secretKey() {
		return DynamicPropertyFactory.getInstance().getStringProperty("secure.secretKey", "hien123456").get();
	}
}
