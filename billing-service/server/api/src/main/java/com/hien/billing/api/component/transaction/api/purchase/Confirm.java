package com.hien.billing.api.component.transaction.api.purchase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.hien.base.exception.InvalidException;
import com.hien.base.exception.ResourceNotFoundException;
import com.hien.base.mvc.CustomRestAPI;
import com.hien.billing.api.component.card.domain.CardRepo;
import com.hien.billing.api.component.transaction.domain.Transaction;
import com.hien.billing.api.component.transaction.domain.TransactionRepo;

@RestController
@RequestMapping(path = { "/api/v1/purchase/confirm" })
public class Confirm {

	@Autowired
	CardRepo cardRepo;

	@Autowired
	TransactionRepo transactionRepo;

	@PostMapping
	@CustomRestAPI
	public Object execute(@RequestBody Request req) {

		Transaction txn = transactionRepo.findOne(req.purchaseId);
		if (txn == null) {
			throw new ResourceNotFoundException("transaction");
		}
		if (!Transaction.Status.confirming.equals(txn.status)) {
			throw new InvalidException("transaction");
		}
		if (!txn.otp.equals(req.otp)) {
			throw new InvalidException("otp");
		}
		// TODO: add more validation here

		txn.status = Transaction.Status.reserved;
		transactionRepo.save(txn);

		return ImmutableMap.of("purchaseId", txn.id);
	}

	public static class Request {
		public Long purchaseId;

		public String otp;
	}
}
