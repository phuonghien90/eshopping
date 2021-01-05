package com.hien.eshopping.api.component.order.api;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.hien.base.exception.ResourceNotFoundException;
import com.hien.base.log.LogObj;
import com.hien.base.mvc.CustomRestAPI;
import com.hien.eshopping.api.component.order.domain.Order;
import com.hien.eshopping.api.component.order.service.PurchaseService;
import com.hien.eshopping.api.component.order.service.SendSmsService;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

@RestController
@RequestMapping(path = { "/api/v1/verify" })
public class Verify {

	@Autowired
	private PurchaseService purchaseService;
	@Autowired
	private SendSmsService sendSmsService;
	@Autowired
	private Executor getVoucherExecutor;

	@PostMapping
	@CustomRestAPI
	public Object execute(@RequestHeader("token") String token,
			@RequestBody Request req,
			LogObj log) {

		Order order = purchaseService.verifyToken(token);
		if (order == null) {
			throw new ResourceNotFoundException("order");
		}
		// TODO: add more validation here

		purchaseService.confirmPurchase(order, req.otp, log);

		MutableBoolean needSendSms = new MutableBoolean(false);

		Observable<String> observable = Observable
				.fromCallable(() -> getVoucher(order, needSendSms, log))
				.subscribeOn(Schedulers.from(getVoucherExecutor));

		try {
			String voucherCode = observable.timeout(30, TimeUnit.SECONDS).blockingFirst();

			return ImmutableMap.of(
					"status", "ok",
					"voucherCode", voucherCode);
		} catch (RuntimeException e) {
			if (e.getCause() instanceof TimeoutException) {
				needSendSms.setTrue();
				return ImmutableMap.of(
						"status", "ok",
						"message", "send voucher code through sms later");
			}
			purchaseService.cancelPurchase(order, log);
			throw e;
		}
	}

	private String getVoucher(Order order, MutableBoolean needSendSms, LogObj log) {

		purchaseService.generateVoucherCode(order, log);

		purchaseService.commitPurchase(order, log);

		if (needSendSms.isTrue()) {
			sendSmsService.sendVoucherAsync(order);
		}

		return order.voucherCode;
	}

	public static class Request {
		public String otp;
	}
}
