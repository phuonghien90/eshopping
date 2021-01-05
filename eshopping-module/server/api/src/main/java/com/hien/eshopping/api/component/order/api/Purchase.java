package com.hien.eshopping.api.component.order.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.hien.base.exception.ResourceNotFoundException;
import com.hien.base.log.LogObj;
import com.hien.base.mvc.CustomRestAPI;
import com.hien.eshopping.api.component.order.domain.Order;
import com.hien.eshopping.api.component.order.service.PurchaseService;
import com.hien.eshopping.api.component.product.domain.Product;
import com.hien.eshopping.api.component.product.domain.ProductRepo;
import com.hien.eshopping.api.dto.BankCard;

@RestController
@RequestMapping(path = { "/api/v1/purchase" })
public class Purchase {

	@Autowired
	ProductRepo productRepo;
	@Autowired
	PurchaseService purchaseService;

	@PostMapping
	@CustomRestAPI
	public Object execute(@RequestBody Request req,
			LogObj log) {

		Product product = productRepo.findOne(ProductRepo.Spec.skuCode(req.skuCode));
		if (product == null) {
			throw new ResourceNotFoundException("product");
		}
		// TODO: add more validation here

		Order order = purchaseService.createOrder(req);

		purchaseService.verifyPurchase(req, product, order, log);

		String token = purchaseService.generateToken(order);

		return ImmutableMap.of(
				"status", "ok",
				"token", token);
	}

	public static class Request {
		public String phoneNumber;

		public String skuCode;

		public BankCard bankCard;
	}
}
